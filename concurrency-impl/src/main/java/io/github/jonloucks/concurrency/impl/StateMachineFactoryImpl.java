package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachine.Config;
import io.github.jonloucks.concurrency.api.StateMachine.Config.Builder;
import io.github.jonloucks.concurrency.api.StateMachineFactory;

import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.Checks.builderConsumerCheck;

final class StateMachineFactoryImpl implements StateMachineFactory {
    
    @Override
    public <T> StateMachine<T> create(Config<T> config) {
        return new StateMachineImpl<>(config);
    }
    
    @Override
    public <T> StateMachine<T> create(Consumer<Builder<T>> builderConsumer) {
        final StateMachineConfigImpl<T> config = new StateMachineConfigImpl<>();
        builderConsumerCheck(builderConsumer).accept(config);
        return create(config);
    }
    
    StateMachineFactoryImpl() {
    }
}
