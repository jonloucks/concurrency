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
    
    WaitableSupplier<T> observeValue();
    
    /**
     * @return the current completion state
     */
    Optional<Completion<T>> getCompletion();
  
    interface Config<T> {
        interface Builder<T> extends Config<T> {
        }
    }
}
