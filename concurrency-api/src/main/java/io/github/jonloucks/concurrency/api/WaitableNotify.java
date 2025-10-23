package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Notify lister when condition is satisfied
 * @param <T> the type of value
 */
public interface WaitableNotify<T> {
    
    /**
     * When condition is satisfied the listener is invoked
     * Note: It is likely the listener will be called within a write lock context.
     * Deadlocks could happen of listener is waiting on another thread to acquire a lock to this WaitableNotify
     *
     * @param predicate the predicate to test if the value should be passed to listener
     * @param listener the listener
     * @return AutoClose which removes the listener
     * @throws IllegalArgumentException if predicate is null or the listener is null
     */
    AutoClose notifyIf(Predicate<T> predicate, Consumer<T> listener);
}
