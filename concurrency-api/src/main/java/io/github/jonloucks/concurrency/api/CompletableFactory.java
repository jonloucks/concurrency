package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contract;

import java.util.function.Consumer;

/**
 * Responsibility: Creating a new Completable
 */
public interface CompletableFactory {
    
    /**
     * The Contract for the CompletableFactory
     */
    Contract<CompletableFactory> CONTRACT = Contract.create(CompletableFactory.class);
    
    /**
     * Create a new Completable
     *
     * @param config the completable configuration
     * @return the new Completable
     * @param <T> the type of completion value
     */
    <T> Completable<T> createCompletable(Completable.Config<T> config);
    
    /**
     * Create a new Completable
     *
     * @param builderConsumer receives the Completable Config Builder
     * @return the new Completable
     * @param <T> the type of completion value
     */
    <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer);
}
