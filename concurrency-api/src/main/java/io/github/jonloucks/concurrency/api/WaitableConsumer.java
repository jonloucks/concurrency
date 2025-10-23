package io.github.jonloucks.concurrency.api;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Constants.MAX_TIMEOUT;

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
    
    /**
     * Assign a new value if conditions are satisfied
     *
     * @param predicate the predicate to test if a value should be replaced
     * @param valueSupplier the supplier of the new value
     * @return if accepted the value replaced.
     * @throws IllegalArgumentException if predicate is null or if value is null
     */
     Optional<T> acceptIf(Predicate<T> predicate, Supplier<T> valueSupplier);
    
    /**
     * Assign a new value if conditions are satisfied
     *
     * @param predicate the predicate to test if a value should be replaced
     * @param valueSupplier the new value supplier
     * @return if accepted the value replaced.
     * @throws IllegalArgumentException if predicate is null or if valueSupplier is null
     */
    default Optional<T> acceptWhen(Predicate<T> predicate, Supplier<T> valueSupplier) {
        return acceptWhen(predicate, valueSupplier, MAX_TIMEOUT);
    }
    
    /**
     * Assign a new value if conditions are satisfied
     *
     * @param predicate the predicate to test if a value should be replaced
     * @param valueSupplier the new value supplier
     * @param timeout how long to wait to for test is satisfied
     * @return if accepted the value replaced.
     * @throws IllegalArgumentException if predicate is null, valueSupplier is null, timeout is null or invalid
     */
    Optional<T> acceptWhen(Predicate<T> predicate, Supplier<T> valueSupplier, Duration timeout);
}
