package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contract;

/**
 * Waitable Factory
 */
public interface WaitableFactory {
    /**
     * The Contract for the WaitableFactory
     */
    Contract<WaitableFactory> CONTRACT = Contract.create(WaitableFactory.class);
    
    /**
     * Create a new Waitable with the given initial value
     * @param initialValue (null is not allowed)
     * @return the waitable
     * @param <T> the type of waitable
     */
    <T> Waitable<T> create(T initialValue);
}
