package io.github.jonloucks.concurrency.api;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Waitable consumer
 * @param <T> the type of value
 */
public interface WaitableConsumer<T> extends Consumer<T> {
    /**
     * Assign a new value
     *
     * @param value the new value
     *  @throws IllegalArgumentException if value is null
     */
    @Override
    void accept(T value);
    
    /**
     * Assign a new value if conditions are satisfied
     *
     * @param predicate the predicate to test if a value should be replaced
     * @param value the new value
     * @return if accepted the value replaced.
     * @throws IllegalArgumentException if predicate is null or if value is null
     */
    Optional<T> acceptIf(Predicate<T> predicate, T value);
}
