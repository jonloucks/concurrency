package io.github.jonloucks.concurrency.impl;


import io.github.jonloucks.concurrency.api.StateMachine.Transition.Builder;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

final class TransitionBuilderImpl<B extends Builder<B,S,R>,S,R> implements Builder<B,S,R> {
    
    @Override
    public TransitionBuilderImpl<B, S, R> event(String event) {
        this.event = event;
        return this;
    }
    
    @Override
    public TransitionBuilderImpl<B, S, R> successState(S state) {
        this.successState = state;
        return this;
    }

    @Override
    public TransitionBuilderImpl<B, S, R> successValue(Supplier<R> valueSupplier) {
        this.successValue = valueSupplier;
        return this;
    }
    
    @Override
    public TransitionBuilderImpl<B, S, R> successValue(Runnable runnable) {
        if (null == runnable) {
            this.successValue = null;
        }  else {
            this.successValue = () -> {
                runnable.run();
                return null;
            };
        }
        return this;
    }

    @Override
    public TransitionBuilderImpl<B, S, R> errorState(S state) {
        this.errorState = state;
        return this;
    }
    
    @Override
    public Builder<B, S, R> errorValue(Supplier<R> valueSupplier) {
        this.errorValue = valueSupplier;
        return this;
    }
    
    @Override
    public Builder<B, S, R> failedState(S state) {
        this.failedState = state;
        return this;
    }
    
    @Override
    public TransitionBuilderImpl<B, S, R> failedValue(Supplier<R> valueSupplier) {
        this.failedValue = valueSupplier;
        return this;
    }
    
    @Override
    public String getEvent() {
        return event;
    }
    
    @Override
    public S getSuccessState() {
        return successState;
    }
    
    @Override
    public Optional<S> getErrorState() {
        return ofNullable(errorState);
    }
    
    @Override
    public Optional<S> getFailedState() {
        return ofNullable(failedState);
    }

    @Override
    public Optional<Supplier<R>> getFailedValue() {
        return ofNullable(failedValue);
    }
    
    @Override
    public Optional<Supplier<R>> getSuccessValue() {
        return ofNullable(successValue);
    }
    
    @Override
    public Optional<Supplier<R>> getErrorValue() {
        return ofNullable(errorValue);
    }
    
    TransitionBuilderImpl() {
    }
    
    private String event;
    private S successState;
    private Supplier<R> successValue;
    private S errorState;
    private Supplier<R> errorValue;
    private S failedState;
    private Supplier<R> failedValue;
}
