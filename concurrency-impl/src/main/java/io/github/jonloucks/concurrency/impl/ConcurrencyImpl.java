package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.*;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Repository;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Idempotent.withClose;
import static io.github.jonloucks.concurrency.api.Idempotent.withOpen;
import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConcurrencyImpl implements Concurrency {
    
    @Override
    public AutoClose open() {
        return withOpen(stateMachine, this::realOpen);
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
    public <T> Completable<T> createCompletable(Completable.Config<T> config) {
        return completableFactory.createCompletable(config);
    }
    
    @Override
    public <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer) {
        return completableFactory.createCompletable(builderConsumer);
    }
    
    @Override
    public <T> Completion<T> createCompletion(Completion.Config<T> config) {
        return completionFactory.createCompletion(config);
    }
    
    @Override
    public <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer) {
        return completionFactory.createCompletion(builderConsumer);
    }
    
    @Override
    public <T> void completeLater(OnCompletion<T> onCompletion, Consumer<OnCompletion<T>> delegate) {
        new CompleteLaterImpl<>(onCompletion,delegate).run();
    }
    
    @Override
    public <T> T completeNow(OnCompletion<T> onCompletion, Supplier<T> successBlock) {
       return new CompleteNowImpl<>(onCompletion,successBlock).run();
    }
    
    ConcurrencyImpl(Config config, Repository repository, boolean autoOpen) {
        final Config validConfig = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = autoOpen ? validRepository.open() : AutoClose.NONE;
        final Contracts contracts = validConfig.contracts();
        this.waitableFactory = contracts.claim(WaitableFactory.CONTRACT);
        this.completionFactory = contracts.claim(CompletionFactory.CONTRACT);
        this.completableFactory = contracts.claim(CompletableFactory.CONTRACT);
        this.stateMachineFactory = contracts.claim(StateMachineFactory.CONTRACT);
        this.stateMachine = createStateMachine(Idempotent.class, Idempotent.OPENABLE);
    }
    
    private AutoClose realOpen() {
        return this::close;
    }

    private void close() {
        withClose(stateMachine, this::realClose);
    }
    
    private void realClose() {
        closeRepository.close();
    }
    
    private final AutoClose closeRepository;
    private final WaitableFactory waitableFactory;
    private final StateMachineFactory stateMachineFactory;
    private final CompletionFactory completionFactory;
    private final CompletableFactory completableFactory;
    private final StateMachine<Idempotent> stateMachine;
}
