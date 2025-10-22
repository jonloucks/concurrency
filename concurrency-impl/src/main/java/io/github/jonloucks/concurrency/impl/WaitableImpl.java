package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Waitable;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.impl.Internal.*;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class WaitableImpl<T> implements Waitable<T> {

    @Override
    public void shutdown() {
        synchronized (simpleLock) {
            isShutdown = true;
            wakeUpWaitingThreads();
        }
    }
    
    @Override
    public Optional<T> getWhen(Predicate<T> predicate) {
        return getWhen(predicate, Duration.ofSeconds(Long.MAX_VALUE));
    }
    
    @Override
    public Optional<T> getWhen(Predicate<T> predicate, Duration timeout) {
        final Predicate<T> validPredicate = predicateCheck(predicate);
        final Duration validDuration = timeoutCheck(timeout);
        
        synchronized (simpleLock) {
            final T currentValue = reference.get();
            if (validPredicate.test(currentValue)) {
                return Optional.of(currentValue);
            } else if (isShutdown || validDuration.isZero()) {
                return Optional.empty();
            }
            return waitForLoop(validPredicate, validDuration);
        }
    }
    
    @Override
    public void accept(T value) {
        final T validValue = valueCheck(value);
        synchronized (simpleLock) {
            setAndNotifyIfChanged(validValue);
        }
    }

    @Override
    public Optional<T> acceptIf(Predicate<T> predicate, T value) {
        final T validValue = valueCheck(value);
        final Predicate<T> validPredicate = predicateCheck(predicate);
        
        synchronized (simpleLock) {
            final T currentValue = reference.get();
            if (validPredicate.test(currentValue)) {
                setAndNotifyIfChanged(validValue);
                return Optional.of(currentValue);
            } else {
                return Optional.empty();
            }
        }
    }
    
    @Override
    public Optional<T> getIf(Predicate<T> predicate) {
        final Predicate<T> validPredicate = predicateCheck(predicate);
        synchronized (simpleLock) {
            final T currentValue = reference.get();
            return validPredicate.test(currentValue) ? Optional.of(currentValue) : Optional.empty();
        }
    }
    
    @Override
    public T get() {
        synchronized (simpleLock) {
            return reference.get();
        }
    }
    
    WaitableImpl(T initialValue) {
        reference.set(valueCheck(initialValue));
    }
    
    private Optional<T> waitForLoop(Predicate<T> predicate, Duration timeout) {
        final Instant start = Instant.now();
        do {
            runWithIgnore(()-> simpleLock.wait(getWaitMillis(timeout, start)));
            final T value = reference.get();
            if (predicate.test(value)) {
                return Optional.of(value);
            }
        } while (shouldKeepWaiting(timeout, start));
        
        return Optional.empty();
    }
    
    private static long getWaitMillis(Duration timeout, Instant start) {
        return Long.max(1, timeout.minus(Duration.between(start, Instant.now())).toMillis());
    }
    
    private boolean shouldKeepWaiting(Duration timeout, Instant start) {
        if (isShutdown) {
            return false;
        }
        return Duration.between(start, Instant.now()).compareTo(timeout) < 0;
    }
    
    private void setAndNotifyIfChanged(T newValue) {
        final T oldValue = reference.getAndSet(newValue);
        if (oldValue != newValue) {
            wakeUpWaitingThreads();
        }
    }
    
    private void wakeUpWaitingThreads() {
        simpleLock.notifyAll();
    }
    
    private static <T> T valueCheck(T t) {
        return nullCheck(t, "Value must be present.");
    }
    
    private final Object simpleLock = new Object();
    private final AtomicReference<T> reference = new AtomicReference<>();
    private volatile boolean isShutdown = false;
}
