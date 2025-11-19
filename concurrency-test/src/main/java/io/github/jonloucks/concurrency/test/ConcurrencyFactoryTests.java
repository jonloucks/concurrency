package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.*;
import static io.github.jonloucks.concurrency.test.Tools.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public interface ConcurrencyFactoryTests {
    
    @Test
    default void concurrencyFactory_install_WithNullConfig_Throws() {
        withContracts(contracts -> {
            final Concurrency.Config config = new Concurrency.Config() {
                @Override
                public Contracts contracts() {
                    return contracts;
                }
            };
            final Repository repository = contracts.claim(Repository.FACTORY).get();
            final ConcurrencyFactory concurrencyFactory = getConcurrencyFactory(config);
            
            assertThrown(IllegalArgumentException.class,
                () -> concurrencyFactory.install(null, repository));
        });
    }
    
    @Test
    default void concurrencyFactory_install_WithNullRepository_Throws() {
        withContracts(contracts -> {
            final Concurrency.Config config = new Concurrency.Config() {
                @Override
                public Contracts contracts() {
                    return contracts;
                }
            };
            final ConcurrencyFactory concurrencyFactory = getConcurrencyFactory(config);
            
            assertThrown(IllegalArgumentException.class,
                () -> concurrencyFactory.install(config, null));
        });
    }
    
    @Test
    default void concurrencyFactory_install_AlreadyBound_DoesNotThrow   () {
        withConcurrency(b -> {}, (contracts, concurrency)-> {
            final Concurrency.Config config = new Concurrency.Config() {
                @Override
                public Contracts contracts() {
                    return contracts;
                }
            };
            
            final Repository repository = contracts.claim(Repository.FACTORY).get();
            final ConcurrencyFactory concurrencyFactory = getConcurrencyFactory(config);
            
            assertDoesNotThrow(() -> concurrencyFactory.install(config, repository));
        });
    }
    
    @Test
    default void concurrencyFactory_install_WithValid_Works() {
        withContracts(contracts -> {
            final Concurrency.Config config = new Concurrency.Config() {
                @Override
                public Contracts contracts() {
                    return contracts;
                }
            };
            
            final Repository repository = contracts.claim(Repository.FACTORY).get();
            final ConcurrencyFactory concurrencyFactory = getConcurrencyFactory(config);
            
            concurrencyFactory.install(config, repository);
            
            try (AutoClose closeRepository = repository.open()) {
                ignore(closeRepository);
                assertObject(contracts.claim(Concurrency.CONTRACT));
            }
        });
    }
}
