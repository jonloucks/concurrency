package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.concurrency.api.WaitableFactory;

final class WaitableFactoryImpl implements WaitableFactory {
    @Override
    public <T> Waitable<T> create(T initialValue) {
        return new WaitableImpl<>(initialValue);
    }
    
    WaitableFactoryImpl() {
    
    }
}
