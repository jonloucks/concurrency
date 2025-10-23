package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.AutoClose;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.impl.Internal.*;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class WaitableImpl<T> implements Waitable<T> {

    @Override
    public void shutdown() {
        synchronized (simpleLock) {
            isShutdown = true;
            notifyValueListeners.forEach(NotifyValueListener::close);
            wakeUpWaitingThreads();
        }
    }
    
    @Override
    public Optional<T> getWhen(Predicate<T> predicate, Duration timeout) {
        final Predicate<T> validPredicate = predicateCheck(predicate);
        final Duration validTimeout = timeoutCheck(timeout);
        
        synchronized (simpleLock) {
            final T currentValue = reference.get();
            if (validPredicate.test(currentValue)) {
                return Optional.of(currentValue);
            } else if (isShutdown || validTimeout.isZero()) {
                return Optional.empty();
            }
            return waitForLoop(validPredicate, validTimeout);
        }
    }
    
    @Override
    public AutoClose notifyIf(Predicate<T> predicate, Consumer<T> listener) {
        final NotifyValueListener<T> notifyValueListener = new NotifyValueListener<>(predicate, listener, notifyValueListeners);
        
        notifyValueListener.process(get());
        
        return notifyValueListener.open();
    }
    
    @Override
    public void accept(T value) {
        final T validValue = valueCheck(value);
        synchronized (simpleLock) {
            setValue(validValue);
        }
    }
    
    @Override
    public Optional<T> acceptIf(Predicate<T> predicate, T value) {
        final T validValue = valueCheck(value);
        final Predicate<T> validPredicate = predicateCheck(predicate);
        
        synchronized (simpleLock) {
            final T currentValue = reference.get();
            if (validPredicate.test(currentValue)) {
                setValue(validValue);
                return Optional.of(currentValue);
            } else {
                return Optional.empty();
            }
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
            final long waitMillis = getWaitMillis(timeout, start, Instant.now());
            runWithIgnore(() -> simpleLock.wait(waitMillis));
            final T value = reference.get();
            if (predicate.test(value)) {
                return Optional.of(value);
            }
        } while (shouldKeepWaiting(timeout, start));
        
        return Optional.empty();
    }

    private boolean shouldKeepWaiting(Duration timeout, Instant start) {
        return !isShutdown && !hasTimedOut(timeout, start, Instant.now());
    }
    
    private void setValue(T newValue) {
        final T oldValue = reference.getAndSet(newValue);
        if (oldValue != newValue) {
            wakeUpWaitingThreads();
            notifyListeners(newValue);
        }
    }
    
    private void notifyListeners(T newValue) {
        if (!notifyValueListeners.isEmpty()) {
            notifyValueListeners.forEach(n -> n.process(newValue));
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
    private final List<NotifyValueListener<T>> notifyValueListeners = new CopyOnWriteArrayList<>();
}
