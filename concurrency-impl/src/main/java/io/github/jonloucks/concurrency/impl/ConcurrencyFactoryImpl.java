package io.github.jonloucks.concurrency.impl;


import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Promisor;
import io.github.jonloucks.contracts.api.Promisors;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.concurrency.api.*;

import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.impl.Internal.lifeCycle;
import static io.github.jonloucks.contracts.api.BindStrategy.ALWAYS;
import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.Checks.*;

/**
 * Creates Concurrency instances
 * Opt-in construction via reflection, ServiceLoader or directly.
 */
public final class ConcurrencyFactoryImpl implements ConcurrencyFactory {
    
    /**
     * Publicly accessible constructor as an entry point into this library.
     * It can be invoked via reflection, ServiceLoader or directly.
     */
    public ConcurrencyFactoryImpl() {
    }
    
    @Override
    public Concurrency create(Concurrency.Config config) {
        final Concurrency.Config validConfig = enhancedConfigCheck(config);
        final Repository repository = validConfig.contracts().claim(Repository.FACTORY).get();
        
        installCore(validConfig, repository);
        
        final ConcurrencyImpl concurrency = new ConcurrencyImpl(validConfig, repository, true);
        repository.keep(Concurrency.CONTRACT, () -> concurrency);
        return concurrency;
    }
    
    @Override
    public Concurrency create(Consumer<Concurrency.Config.Builder> builderConsumer) {
        final Consumer<Concurrency.Config.Builder> validBuilderConsumer = builderConsumerCheck(builderConsumer);
        final ConfigBuilderImpl builder = new ConfigBuilderImpl();
        
        validBuilderConsumer.accept(builder);
        
        return create(builder);
    }
    
    @Override
    public void install(Concurrency.Config config, Repository repository) {
        final Concurrency.Config validConfig = enhancedConfigCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        
        installCore(validConfig, validRepository);
        
        final Promisor<Concurrency> concurrencyPromisor = lifeCycle(config.contracts(),
            () -> new ConcurrencyImpl(validConfig, validRepository, false));
        
        validRepository.keep(Concurrency.CONTRACT, concurrencyPromisor, ALWAYS);
    }
  
    private Concurrency.Config enhancedConfigCheck(Concurrency.Config config) {
        final Concurrency.Config candidateConfig = configCheck(config);
        final Contracts contracts = contractsCheck(candidateConfig.contracts());
        
        if (contracts.isBound(Concurrency.CONTRACT)) {
            throw new ConcurrencyException("Concurrency is already bound.");
        }
        
        return candidateConfig;
    }
    
    private void installCore(Concurrency.Config config, Repository repository) {
        final Contracts contracts = contractsCheck(config.contracts());

        repository.require(Repository.FACTORY);
        repository.require(Promisors.CONTRACT);
        repository.require(Idempotent.FACTORY);
        
        repository.keep(Idempotent.FACTORY, () -> IdempotentImpl::new, IF_NOT_BOUND);
        repository.keep(Concurrency.Config.Builder.FACTORY, () -> ConfigBuilderImpl::new, IF_NOT_BOUND);
        repository.keep(ConcurrencyFactory.CONTRACT, lifeCycle(contracts,ConcurrencyFactoryImpl::new), IF_NOT_BOUND);
    }
}
