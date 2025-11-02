package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;

import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.jonloucks.concurrency.impl.Internal.stateCheck;
import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")

final class ImmutableCompletionImpl<T> implements Completion<T> {
    @Override
    public State getState() {
        return state;
    }
    
    @Override
    public Optional<Throwable> getThrown() {
        return thrown;
    }
    
    @Override
    public Optional<T> getValue() {
        return value;
    }
    
    @Override
    public Optional<Future<T>> getFuture() {
        return future;
    }
    
    ImmutableCompletionImpl(Completion.Config<T> config) {
        final Completion.Config<T> validConfig = configCheck(config);
        this.state = stateCheck(validConfig.getState());
        this.thrown = nullCheck(validConfig.getThrown(), "Optional thrown must be present.");
        this.value = nullCheck(validConfig.getValue(), "Optional value must be present.");
        this.future = nullCheck(validConfig.getFuture(), "Optional future must be present.");
    }
    
    private final Completion.State state;
    private final Optional<Throwable> thrown;
    private final Optional<T> value;
    private final Optional<Future<T>> future;
}
