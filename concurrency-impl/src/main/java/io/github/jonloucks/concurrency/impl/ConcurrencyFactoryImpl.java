package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.*;
import io.github.jonloucks.concurrency.api.*;

import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.Checks.*;
import static io.github.jonloucks.contracts.api.GlobalContracts.*;

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
        final Concurrency.Config validConfig = configCheck(config);
        final Repository repository = validConfig.contracts().claim(Repository.FACTORY).get();
        
        installCore(validConfig, repository);
        
        final ConcurrencyImpl concurrency = new ConcurrencyImpl(validConfig, repository, true);
        repository.keep(Concurrency.CONTRACT, () -> concurrency, IF_NOT_BOUND);
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
        final Concurrency.Config validConfig = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        
        installCore(validConfig, validRepository);
        
        final Promisor<Concurrency> concurrencyPromisor = lifeCycle(() -> new ConcurrencyImpl(validConfig, validRepository, false));
        
        validRepository.keep(Concurrency.CONTRACT, concurrencyPromisor, IF_NOT_BOUND);
    }
    
    private void installCore(Concurrency.Config config, Repository repository) {
        repository.require(Repository.FACTORY);
        
        repository.keep(WaitableFactory.CONTRACT, lifeCycle(WaitableFactoryImpl::new), IF_NOT_BOUND);
        repository.keep(StateMachineFactory.CONTRACT, StateMachineFactoryImpl::new, IF_NOT_BOUND);
        repository.keep(Completions.CONTRACT, () -> new CompletionsImpl(config), IF_NOT_BOUND);
        repository.keep(Concurrency.Config.Builder.FACTORY, () -> ConfigBuilderImpl::new, IF_NOT_BOUND);
        repository.keep(ConcurrencyFactory.CONTRACT, lifeCycle(ConcurrencyFactoryImpl::new), IF_NOT_BOUND);
    }
}
