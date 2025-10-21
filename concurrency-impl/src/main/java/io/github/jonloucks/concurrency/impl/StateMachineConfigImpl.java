package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachine.Rule;

import java.util.*;

import static io.github.jonloucks.concurrency.impl.Internal.ruleCheck;
import static io.github.jonloucks.concurrency.impl.Internal.stateCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

final class StateMachineConfigImpl<T> implements StateMachine.Config.Builder<T> {
    @Override
    public Builder<T> initial(T state) {
        initialState = state;
        ofNullable(initialState).ifPresent(this::state);
        return this;
    }
    
    @Override
    public Builder<T> state(T state) {
        final T validState = stateCheck(state);
        this.states.add(validState);
        return this;
    }
    
    @Override
    public Builder<T> states(List<T> states) {
        final List<T> validStates = nullCheck(states, "State list must be present.");
        this.states.addAll(validStates);
        return this;
    }
    
    @Override
    public Builder<T> rule(T state, Rule<T> rule) {
        final T validState = stateCheck(state);
        final Rule<T> validRule = ruleCheck(rule);
        rulesMap.computeIfAbsent(validState, k -> new ArrayList<>()).add(validRule);
        return this;
    }
    
    @Override
    public Builder<T> rules(T state, List<Rule<T>> rules) {
        final T validState = stateCheck(state);
        final List<Rule<T>> validRules = nullCheck(rules, "Rule list must be present.");
        validRules.forEach(r -> rule(validState, r));
        return this;
    }
    
    @Override
    public Optional<T> getInitial() {
        return ofNullable(initialState);
    }
    
    @Override
    public List<Rule<T>> getStateRules(T state) {
        final T validState = stateCheck(state);
        return rulesMap.getOrDefault(validState, emptyList());
    }
    
    @Override
    public List<T> getStates() {
        return new ArrayList<>(states);
    }
    
    StateMachineConfigImpl() {
    }
    
    private T initialState;
    private final Set<T> states = new HashSet<>();
    private final HashMap<T, List<Rule<T>>> rulesMap = new HashMap<>();
}
