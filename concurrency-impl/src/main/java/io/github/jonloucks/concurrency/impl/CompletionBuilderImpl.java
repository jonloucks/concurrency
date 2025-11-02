package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;

import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Optional.ofNullable;

final class CompletionBuilderImpl<T> implements Completion.Config.Builder<T> {
    @Override
    public CompletionBuilderImpl<T> state(Completion.State state) {
        this.state = nullCheck(state, "State must be present.");
        return this;
    }
    
    @Override
    public CompletionBuilderImpl<T> thrown(Throwable thrown) {
        this.thrown = thrown;
        return this;
    }
    
    @Override
    public CompletionBuilderImpl<T> value(T value) {
        this.value = value;
        return this;
    }
    
    @Override
    public Builder<T> future(Future<T> future) {
        this.future = future;
        return this;
    }
    
    @Override
    public Completion.State getState() {
        return state;
    }
    
    @Override
    public Optional<Throwable> getThrown() {
        return ofNullable(thrown);
    }
    
    @Override
    public Optional<T> getValue() {
        return ofNullable(value);
    }
    
    @Override
    public Optional<Future<T>> getFuture() {
        return ofNullable(future);
    }
    
    private Throwable thrown;
    private T value;
    private Completion.State state = Completion.State.DELEGATED;
    private Future<T> future;
}
