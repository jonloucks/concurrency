package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoOpen;

import java.util.Optional;

/**
 * Responsibility: Observe a single activity from start to finish
 *
 * @param <T> the type of completion value
 */
public interface Completable<T> extends AutoOpen, CompletionNotify<T>, OnCompletion<T> {
    
    /**
     * @return true if completed
     */
    boolean isCompleted();
    
    /**
     * @return observe state change
     */
    WaitableSupplier<Completion.State> observeState();
    
    /**
     * @return Observe the completed value
     */
    WaitableSupplier<T> observeValue();
    
    /**
     * @return the current completion state
     */
    Optional<Completion<T>> getCompletion();
    
    /**
     * Configuration used to create a new Completable
     * @param <T> the type of value
     */
    interface Config<T> {
        
        /**
         * Configuration builder used to create a new Completeable
         * @param <T> the type of value
         */
        interface Builder<T> extends Config<T> {
        }
    }
}
