package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completable;
import io.github.jonloucks.concurrency.api.CompletableFactory;
import io.github.jonloucks.concurrency.api.Concurrency;

import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.Checks.builderConsumerCheck;
import static io.github.jonloucks.contracts.api.Checks.configCheck;

final class CompletableFactoryImpl implements CompletableFactory {
    
    @Override
    public <T> Completable<T> createCompletable(Completable.Config<T> config) {
        return new CompletableImpl<>(this.config, configCheck(config));
    }
    
    @Override
    public <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer) {
        final Consumer<Completable.Config.Builder<T>> validBuilderConsumer = builderConsumerCheck(builderConsumer);
        final Completable.Config.Builder<T> builder = new Completable.Config.Builder<>() {};
        validBuilderConsumer.accept(builder);
        return createCompletable(builder);
    }
    
    CompletableFactoryImpl(Concurrency.Config config) {
        this.config = config;
    }
    
    private final Concurrency.Config config;
}
