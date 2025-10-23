package io.github.jonloucks.concurrency.api;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.api.Constants.MIN_TIMEOUT;

/**
 * Provides mutable reference that allows other threads to wait until
 * the value satisfies a given condition.
 *
 * @param <T> the type of references
 */
public interface Waitable<T> extends WaitableSupplier<T>, WaitableConsumer<T>, WaitableNotify<T> {

    /**
     * Waits until condition is satisfied for a value to match the predicate
     *
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @return optionally the value if
     * @throws IllegalArgumentException if predicate is null
     * @deprecated Replaced by {@link #getWhen(Predicate)}
     */
    @Deprecated
    default Optional<T> waitFor(Predicate<T> predicate) {
        return getWhen(predicate, MIN_TIMEOUT);
    }

    /**
     * Waits for given timeout for a value to match the predicate
     *
     * @param predicate the predicate to test if the value satisfies the stop waiting condition
     * @param timeout the time to wait for the value to satisfy the predicate
     * @return optionally the value if
     * @throws IllegalArgumentException if predicate is null, duration is null, or duration is negative
     * @deprecated Replaced by {@link #getWhen(Predicate, Duration)}
     */
    @Deprecated 
    default Optional<T> waitFor(Predicate<T> predicate, Duration timeout) {
        return getWhen(predicate, timeout);
    }
    
    /**
     * Aborts all waiting threads.
     * All subsequent wait related calls will return immediately.
     * Shutdown is permanent
     */
    void shutdown();
}


