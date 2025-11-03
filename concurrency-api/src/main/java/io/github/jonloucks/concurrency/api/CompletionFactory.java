package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contract;

import java.util.function.Consumer;

/**
 * Responsibility: Creating a new Completion
 */
public interface CompletionFactory {
    
    /**
     * The Contract for the CompletionFactory
     */
    Contract<CompletionFactory> CONTRACT = Contract.create(CompletionFactory.class);
    
    /**
     * Create a new Completion
     *
     * @param builderConsumer receives the Completion Config Builder
     * @return the new Completable
     * @param <T> the type of completion value
     */
    <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer);
    
    /**
     * Create a new Completion
     *
     * @param config the Completion configuration
     * @return the new Completable
     * @param <T> the type of completion value
     */
    <T> Completion<T> createCompletion(Completion.Config<T> config);
}
