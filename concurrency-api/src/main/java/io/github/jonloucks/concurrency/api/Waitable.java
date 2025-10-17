package io.github.jonloucks.concurrency.api;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Waitable<T> extends Supplier<T>, Consumer<T> {
    
    /**
     * @return Get current value
     */
    @Override
    T get();
    
    /**
     * Gets the current value if it matches the current value
     * @param predicate the predicate
     * @return optionally get the current value if it matches
     */
    Optional<T> getIf(Predicate<T> predicate);
    
    /**
     * Change the current value
     * @param value the new value
     */
    @Override
    void accept(T value);
    
    /**
     *
     * @param predicate the predicate to test if a value should be replaced
     * @param value the new value
     * @return if accepted the value replaced.
     */
    Optional<T> acceptIf(Predicate<T> predicate, T value);
    
    void shutdown();
    
    /**
     * Waits forever for a value to match the predicate
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @return optionally the value if
     */
    Optional<T> waitFor(Predicate<T> predicate);
    
    /**
     * Waits for given timeout for a value to match the predicate
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @param timeout the time to wait for the value to satisfy the predicate
     * @return optionally the value if
     */
    Optional<T> waitFor(Predicate<T> predicate, Duration timeout);
}


