package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.Waitable;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.impl.Internal.*;
import static io.github.jonloucks.contracts.api.Checks.*;
import static java.util.Optional.ofNullable;

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
    public S get() {
        return currentState.get();
    }
    
    @Override
    public Optional<S> getIf(Predicate<S> predicate) {
        return currentState.getIf(predicate);
    }
    
    @Override
    public Optional<S> getWhen(Predicate<S> predicate) {
        return currentState.getWhen(predicate);
    }
    
    @Override
    public Optional<S> getWhen(Predicate<S> predicate, Duration timeout) {
        return currentState.getWhen(predicate, timeout);
    }
    
    @Override
    public <B extends Transition.Builder<B, S, R>, R> R transition(Consumer<Transition.Builder<B, S, R>> builderConsumer) {
        final TransitionBuilderImpl<B,S,R> builder = new TransitionBuilderImpl<>();
        builderConsumerCheck(builderConsumer).accept(builder);
        return transition(builder);
    }
    
    @Override
    public <R> R transition(Transition<S, R> transition) {
        final Transition<S,R> t = transitionCheck(transition);
        if (isTransitionAllowed(t.getEvent(), t.getSuccessState())) {
            try {
                return handleSuccess(t);
            } catch (Throwable thrown) {
                return handleError(t, thrown);
            }
        } else {
            return handleDenied(t);
        }
    }
    
    @Override
    public boolean hasState(S state) {
        return stateToRulesLookup.containsKey(stateCheck(state));
    }
    
    @Override
    public boolean isTransitionAllowed(String event, S state) {
        final String validEvent = Internal.eventCheck(event);
        final S toState = stateCheck(state);
        final S fromState = getState();
        if (hasState(toState) && !fromState.equals(toState)) {
            final Set<Rule<S>> rules = stateToRulesLookup.get(fromState);
            if (ofNullable(rules).isPresent() && !rules.isEmpty()) {
                return rules.stream().allMatch(r -> r.canTransition(validEvent, toState));
            }
            return true;
        }
        return false;
    }
    
    StateMachineImpl(Config<S> config) {
        final Config<S> validConfig = configCheck(config);
        final S validInitialState = validConfig.getInitial().orElseThrow(this::getInitialStateNotPresentException);
        this.currentState = new WaitableImpl<>(validInitialState);
        addStateAndRules(validInitialState, Collections.emptyList() );
        validConfig.getStates().forEach(state -> addStateAndRules(state, validConfig.getStateRules(state)));
    }
    
    private <R> Transition<S,R> transitionCheck(Transition<S,R> transition) {
        final Transition<S,R> validTransition = nullCheck(transition, "Transition must be present.");
        
        existsCheck(validTransition.getSuccessState());
        ofNullable(transition.getEvent()).orElseThrow(this::getEventNotPresentException);
        
        return validTransition;
    }
    
    private <R> R handleSuccess(Transition<S, R> t) {
        final R value = orNull(t.getSuccessValue());
        setState(t.getEvent(), t.getSuccessState());
        return value;
    }
    
    private <R> R handleDenied(Transition<S, R> transition) {
        setOptionalState(transition.getFailedState(), transition.getEvent());
        if (transition.getFailedValue().isPresent()) {
            return transition.getFailedValue().get().get();
        }
        throw new ConcurrencyException("Illegal state transition from " + getState() +
            " to " + transition.getSuccessState() + ".");
    }
    
    private <R> R handleError(Transition<S, R> t, Throwable thrown) throws Error, ConcurrencyException, RuntimeException {
        setOptionalState(t.getErrorState(), t.getEvent());
        if (t.getErrorValue().isPresent()) {
            return t.getErrorValue().get().get();
        } else {
            throwUnchecked(thrown, "State machine error.");
            return null;
        }
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <X> X orNull(Optional<Supplier<X>> optional) {
        return optional.map(Supplier::get).orElse(null);
    }
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private <R> void setOptionalState(Optional<S> optional, String event) {
        optional.ifPresent(s -> setState(event, s));
    }
    
    private S existsCheck(S state) {
        final S validState = stateCheck(state);
        return illegalCheck(validState, !hasState(validState), "Rule does not exist.");
    }
    
    private IllegalArgumentException getInitialStateNotPresentException() {
        return new IllegalArgumentException("Initial state must be present.");
    }
    
    private IllegalArgumentException getEventNotPresentException() {
        return new IllegalArgumentException("Event must be present.");
    }
    
    private void addStateAndRules(S state, List<Rule<S>> rules) {
        final S validState = stateCheck(state);
        final List<Rule<S>> validRules = nullCheck(rules, "Rules must be present.");
        final Set<Rule<S>> knownRules = stateToRulesLookup(validState);
        validRules.forEach(rule -> knownRules.add(ruleCheck(rule)));
    }
    
    private Set<Rule<S>> stateToRulesLookup(S state) {
        return stateToRulesLookup.computeIfAbsent(state, k -> new HashSet<>());
    }
    
    private final HashMap<S, Set<Rule<S>>> stateToRulesLookup = new HashMap<>();
    private final Waitable<S> currentState;
}
