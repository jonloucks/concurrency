package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.contracts.test.BadContractsFactoryTests;
import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;

import java.util.function.Consumer;

/**
 * Used to introduce errors.
 * 1. Class is not public
 * 2. create throws an exception
 * 3. Constructor is not public
 * @see BadContractsFactoryTests
 */
final class BadConcurrencyFactory implements ConcurrencyFactory {
    @Override
    public Concurrency create(Concurrency.Config config) {
        throw new UnsupportedOperationException("Not supported ever.");
    }
    
    @Override
    public Concurrency create(Consumer<Concurrency.Config.Builder> builderConsumer) {
        throw new UnsupportedOperationException("Not supported ever.");
    }
    
    @Override
    public void install(Concurrency.Config config, Repository repository) {
        throw new UnsupportedOperationException("Not supported ever.");
    }
    
    BadConcurrencyFactory() {
    }
}
