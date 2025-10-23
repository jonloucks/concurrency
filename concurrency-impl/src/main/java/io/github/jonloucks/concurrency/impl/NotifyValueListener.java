package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.impl.Internal.*;

final class NotifyValueListener<T> {
    
    void process(T value) {
        if (isActive() && predicate.test(value)) {
            listener.accept(value);
        }
    }

    NotifyValueListener(Predicate<T> predicate, Consumer<T> listener, List<NotifyValueListener<T>> ownerList) {
        this.predicate = predicateCheck(predicate);
        this.listener = listenerCheck(listener);
        this.ownerList = ownerList;
    }
    
    AutoClose open() {
        ownerList.add(this);
        return this::close;
    }
    
    void close() {
        if (isClosed.compareAndSet(false, true)) {
            ownerList.removeIf(x -> x == this);
        }
    }
    
    private boolean isActive() {
        return !isClosed.get();
    }
    
    private final Predicate<T> predicate;
    private final Consumer<T> listener;
    private final List<NotifyValueListener<T>> ownerList;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
}
