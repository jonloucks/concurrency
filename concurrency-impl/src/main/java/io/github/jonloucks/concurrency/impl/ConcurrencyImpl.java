package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.*;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Repository;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConcurrencyImpl implements Concurrency {
    
    @Override
    public AutoClose open() {
        return stateMachine.transition(b -> b
            .event("open")
            .goalState(Idempotent.OPENED)
            .action(this::realOpen)
       .orElse(AutoOpen.NONE::open));
    }
    
    @Override
    public <T> Waitable<T> createWaitable(T initialValue) {
        return waitableFactory.create(initialValue);
    }
    
    @Override
    public <T> StateMachine<T> createStateMachine(T initialState) {
        return stateMachineFactory.create(initialState);
    }
    
    @Override
    public <T extends Enum<T>> StateMachine<T> createStateMachine(Class<T> enumClass, T initialState) {
        return stateMachineFactory.create(enumClass, initialState);
    }

    ConcurrencyImpl(Config config, Repository repository, boolean autoOpen) {
        final Config validConfig = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = autoOpen ? validRepository.open() : AutoClose.NONE;
        this.waitableFactory = validConfig.contracts().claim(WaitableFactory.CONTRACT);
        this.stateMachineFactory = validConfig.contracts().claim(StateMachineFactory.CONTRACT);
        this.stateMachine = stateMachineFactory.create(Idempotent.class, Idempotent.NEW);
    }
    
    private AutoClose realOpen() {
        return this::close;
    }

    private void close() {
        stateMachine.transition(b -> b
            .event("close")
            .goalState(Idempotent.CLOSED)
            .action(this::realClose));
    }
    
    private void realClose() {
        closeRepository.close();
    }
    
    private final AutoClose closeRepository;
    private final WaitableFactory waitableFactory;
    private final StateMachineFactory stateMachineFactory;
    private final StateMachine<Idempotent> stateMachine;
}
