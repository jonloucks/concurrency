package io.github.jonloucks.concurrency.impl;


import io.github.jonloucks.concurrency.api.Transition.Builder;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

final class TransitionImpl<B extends Builder<B,S,R>,S,R> implements Builder<B,S,R> {
    
    @Override
    public TransitionImpl<B, S, R> event(String name) {
        this.event = name;
        return this;
    }
    
    @Override
    public TransitionImpl<B, S, R> goalState(S goalState) {
        this.goalState = goalState;
        return this;
    }

    @Override
    public TransitionImpl<B, S, R> action(Supplier<R> action) {
        this.action = action;
        return this;
    }
    
    @Override
    public TransitionImpl<B, S, R> action(Runnable action) {
        if (null == action) {
            this.action = null;
        }  else {
            this.action = () -> {
                action.run();
                return null;
            };
        }
        return this;
    }

    @Override
    public TransitionImpl<B, S, R> errorState(S state) {
        this.errorState = state;
        return this;
    }
    
    @Override
    public Builder<B, S, R> orElse(Supplier<R> orElse) {
        this.orElse = orElse;
        return this;
    }
    
    @Override
    public Builder<B, S, R> rethrow(boolean rethrow) {
        this.rethrow = rethrow;
        return this;
    }
    
    @Override
    public Optional<String> event() {
        return ofNullable(event);
    }
    
    @Override
    public S goalState() {
        return goalState;
    }
    
    @Override
    public Optional<S> errorState() {
        return ofNullable(errorState);
    }
    
    @Override
    public boolean rethrow() {
        return rethrow;
    }
    
    @Override
    public Optional<Supplier<R>> orElse() {
        return ofNullable(orElse);
    }
    
    @Override
    public Optional<Supplier<R>> action() {
        return ofNullable(action);
    }

    TransitionImpl() {
    }
    
    private S goalState;
    private Supplier<R> action;
    private S errorState;
    private String event;
    private Supplier<R> orElse;
    private boolean rethrow = true;
}
