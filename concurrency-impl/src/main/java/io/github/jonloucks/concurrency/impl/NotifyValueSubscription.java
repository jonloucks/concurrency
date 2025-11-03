package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.impl.Internal.*;

final class NotifyValueSubscription<T> {
    
    void process(T value) {
        if (isActive() && predicate.test(value)) {
            listener.accept(value);
        }
    }

    NotifyValueSubscription(Predicate<T> predicate, Consumer<T> listener, List<NotifyValueSubscription<T>> ownerList) {
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
            removeExact(ownerList,this);
        }
    }
    
    private boolean isActive() {
        return !isClosed.get();
    }
    
    private final Predicate<T> predicate;
    private final Consumer<T> listener;
    private final List<NotifyValueSubscription<T>> ownerList;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
}
