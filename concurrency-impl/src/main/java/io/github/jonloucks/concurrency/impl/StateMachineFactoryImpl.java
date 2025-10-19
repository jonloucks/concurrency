package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachineFactory;

final class StateMachineFactoryImpl implements StateMachineFactory {
    @Override
    public <T> StateMachine<T> create(T initialState) {
        return new StateMachineImpl<>(initialState);
    }
    
    StateMachineFactoryImpl() {
    
    }
}
