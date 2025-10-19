package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contract;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * State machine, containing a set of sets that can be transitioned to by events/actions
 */
public interface StateMachineFactory {
    Contract<StateMachineFactory> CONTRACT = Contract.create(StateMachineFactory.class);
    
    /**
     * Create a new StateMachine
     *
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of each state
     */
    <T> StateMachine<T> create(T initialState);
    
    /**
     * Create a new StateMachine
     *
     * @param enumClass the enumeration class
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of state
     */
    default <T extends Enum<T>> StateMachine<T> create(Class<T> enumClass, T initialState) {
        final Class<T> validEnumClass = nullCheck(enumClass, "Enum class must be present.");
        final StateMachine<T> stateMachine = create(initialState);
        for (T t : validEnumClass.getEnumConstants()) {
            stateMachine.addState(t);
        }
        return stateMachine;
    }
}
