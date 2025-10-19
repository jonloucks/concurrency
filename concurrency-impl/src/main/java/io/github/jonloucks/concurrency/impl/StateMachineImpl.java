package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.Transition;
import io.github.jonloucks.concurrency.api.TransitionAware;
import io.github.jonloucks.concurrency.api.Waitable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.impl.Internal.*;
import static io.github.jonloucks.contracts.api.Checks.*;

final class StateMachineImpl<S> implements StateMachine<S> {
    
    @Override
    public boolean setState(String event, S state) {
        if (isTransitionAllowed(event, existsCheck(state))) {
            currentState.accept(state);
            return true;
        }
        return false;
    }
    
    @Override
    public S getState() {
        return currentState.get();
    }
    
    @Override
    public <B extends Transition.Builder<B, S, R>, R> R transition(Consumer<Transition.Builder<B, S, R>> builderConsumer) {
        final Transition.Builder<B,S,R> builder = new TransitionImpl<>();
        builderConsumerCheck(builderConsumer).accept(builder);
        return transition(builder);
    }
    
    @Override
    public <R> R transition(Transition<S, R> transition) {
        final Transition<S,R> validTransition = transitionCheck(transition);
        final S goalState = getGoalState(validTransition);
        final String event = getEvent(validTransition);
        
        if (isTransitionAllowed(event, goalState)) {
            try {
                final R r = getValue(validTransition.action());
                setState(event, goalState);
                return r;
            } catch (Throwable thrown) {
                if (validTransition.errorState().isPresent()) {
                    setState(event, validTransition.errorState().get());
                }
                if (transition.rethrow()) {
                    throw thrown;
                }
            }
        }
        return getValue(validTransition.orElse());
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <X> X getValue(Optional<Supplier<X>> optionalSupplier) {
        return optionalSupplier.map(Supplier::get).orElse(null);
    }
    
    @Override
    public void addState(S state) {
        stateSet.add(stateCheck(state));
    }
    
    @Override
    public boolean hasState(S state) {
        return stateSet.contains(stateCheck(state));
    }
    
    @Override
    public boolean isTransitionAllowed(String event, S state) {
        final String validEvent = eventCheck(event);
        final S validState = stateCheck(state);
        final S currentState = getState();
        if (hasState(validState) && !currentState.equals(validState)) {
            if (state instanceof TransitionAware) {
                return ((TransitionAware)currentState).canTransitionTo(validEvent, validState);
            }
            return true;
        }
        return false;
    }
    
    StateMachineImpl(S initialState) {
        final S validState = stateCheck(initialState);
        this.currentState = new WaitableImpl<>(validState);
        stateSet.add(validState);
    }
    
    private <R> S getGoalState(Transition<S, R> transition) {
        return existsCheck(transition.goalState());
    }
    
    private <R> String getEvent(Transition<S, R> transition) {
        return transition.event().orElseThrow(this::getEventNotPresentException);
    }
    
    S existsCheck(S state) {
        final S validState = stateCheck(state);
        return illegalCheck(validState, !hasState(validState), "State does not exist.");
    }
    
    private IllegalArgumentException getEventNotPresentException() {
        return new IllegalArgumentException("Event must be present.");
    }
    
    private final Set<S> stateSet = new HashSet<>();
    private final Waitable<S> currentState;
}
