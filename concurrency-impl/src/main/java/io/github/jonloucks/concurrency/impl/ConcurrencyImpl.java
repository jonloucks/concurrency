package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.*;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Repository;

import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConcurrencyImpl implements Concurrency {
    
    @Override
    public AutoClose open() {
        return stateMachine.transition(b -> b
            .event("open")
            .successState(Idempotent.OPENED)
            .successValue(this::realOpen)
       .failedValue(AutoOpen.NONE::open));
    }
    
    @Override
    public <T> Waitable<T> createWaitable(T initialValue) {
        return waitableFactory.create(initialValue);
    }
    
    @Override
    public <T> StateMachine<T> createStateMachine(T initialState) {
        return stateMachineFactory.create(b -> b.initial(initialState));
    }
    
    @Override
    public <T extends Enum<T>> StateMachine<T> createStateMachine(Class<T> enumClass, T initialState) {
        return stateMachineFactory.create(enumClass, initialState);
    }
    
    @Override
    public <T> StateMachine<T> createStateMachine(Consumer<StateMachine.Config.Builder<T>> builderConsumer) {
        return stateMachineFactory.create(builderConsumer);
    }
    
    @Override
    public <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer) {
        return completions.createCompletable(builderConsumer);
    }
    
    @Override
    public <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer) {
        return completions.createCompletion(builderConsumer);
    }
    
    ConcurrencyImpl(Config config, Repository repository, boolean autoOpen) {
        final Config validConfig = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = autoOpen ? validRepository.open() : AutoClose.NONE;
        this.waitableFactory = validConfig.contracts().claim(WaitableFactory.CONTRACT);
        this.stateMachineFactory = validConfig.contracts().claim(StateMachineFactory.CONTRACT);
        this.completions = validConfig.contracts().claim(Completions.CONTRACT);
        this.stateMachine = createStateMachine(Idempotent.class, Idempotent.OPENABLE);
    }
    
    private AutoClose realOpen() {
        return this::close;
    }

    private void close() {
        stateMachine.transition(b -> b
            .event("close")
            .successState(Idempotent.CLOSED)
            .successValue(this::realClose));
    }
    
    private void realClose() {
        closeRepository.close();
    }
    
    private final AutoClose closeRepository;
    private final WaitableFactory waitableFactory;
    private final StateMachineFactory stateMachineFactory;
    private final StateMachine<Idempotent> stateMachine;
    private final Completions completions;
}
