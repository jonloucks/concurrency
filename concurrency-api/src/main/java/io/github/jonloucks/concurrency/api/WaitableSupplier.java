package io.github.jonloucks.concurrency.api;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Waitable supplier
 * @param <T> the type of value supplied
 */
public interface WaitableSupplier<T> extends Supplier<T> {
    
    /**
     * @return Get current value
     */
    @Override
    T get();
    
    /**
     * Gets the current value if it satisfies a condition
     *
     * @param predicate the predicate
     * @return the current value iif the condition is satisfied
     * @throws IllegalArgumentException if predicate is null or if value is null
     */
    Optional<T> getIf(Predicate<T> predicate);
    
    /**
     * Waits until the current value if it satisfies a condition
     *
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @return the current value iif the condition is satisfied
     * @throws IllegalArgumentException if predicate is null
     */
    Optional<T> getWhen(Predicate<T> predicate);
    
    /**
     * Waits until the current value if it satisfies a condition or a timeout is reached
     *
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @param timeout the time to wait for the value to satisfy the predicate
     * @return the current value iif the condition is satisfied
     * @throws IllegalArgumentException if predicate is null, duration is null, or duration is negative
     */
    Optional<T> getWhen(Predicate<T> predicate, Duration timeout);
}
