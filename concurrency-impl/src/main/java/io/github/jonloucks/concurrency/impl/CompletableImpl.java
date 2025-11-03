package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.*;
import io.github.jonloucks.concurrency.api.Completion.State;
import io.github.jonloucks.contracts.api.AutoClose;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.jonloucks.concurrency.api.Idempotent.withClose;
import static io.github.jonloucks.concurrency.api.Idempotent.withOpen;
import static io.github.jonloucks.concurrency.impl.Internal.completionCheck;
import static java.util.Optional.ofNullable;

final class CompletableImpl<T> implements Completable<T>, OnCompletion<T> {
    
    @Override
    public boolean isCompleted() {
        return completionStateMachine.getState().isCompleted();
    }
    
    @Override
    public WaitableNotify<State> notifyState() {
        return completionStateMachine;
    }
    
    @Override
    public WaitableNotify<T> notifyValue() {
        return waitableValue;
    }
    
    @Override
    public Optional<Completion<T>> getCompletion() {
        return ofNullable(completion);
    }
    
    @Override
    public void onCompletion(Completion<T> completion) {
        if (idempotentStateMachine.getState().isRejecting()) {
            throw new IllegalStateException("Completable must be open.");
        }
        final Completion<T> validCompletion = completionCheck(completion);
        if (completionStateMachine.setState("onCompletion", validCompletion.getState())) {
            this.completion = validCompletion;
            this.waitableValue.accept(validCompletion.getValue().orElse(null));
            subscriptions.forEach(s -> s.onCompletion(validCompletion));
        }
    }

    @Override
    public AutoClose notify(OnCompletion<T> onCompletion) {
        return new NotifyCompletionSubscription<>(onCompletion, subscriptions).open();
    }
    
    @Override
    public AutoClose open() {
        return withOpen(idempotentStateMachine, this::realOpen);
    }
  
    void close() {
        withClose(idempotentStateMachine, this::realClose);
    }
    
    CompletableImpl(Concurrency.Config concurrencyConfig, Completable.Config<T> ignored) {
        this.completionStateMachine = State.createStateMachine(concurrencyConfig.contracts());
        this.idempotentStateMachine = Idempotent.createStateMachine(concurrencyConfig.contracts());
        this.waitableValue = GlobalConcurrency.createWaitable(null);
    }
    
    private AutoClose realOpen() {
        return this::close;
    }
    
    private void realClose() {
        subscriptions.forEach(NotifyCompletionSubscription::close);
    }
    
    private final StateMachine<State> completionStateMachine;
    private final StateMachine<Idempotent> idempotentStateMachine;
    private Completion<T> completion;
    private final Waitable<T> waitableValue;
    private final List<NotifyCompletionSubscription<T>> subscriptions = new CopyOnWriteArrayList<>();
}
