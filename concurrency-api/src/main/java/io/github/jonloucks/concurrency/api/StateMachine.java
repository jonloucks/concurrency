package io.github.jonloucks.concurrency.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * State machine.
 * User defined states with rules to restrict state transitions.
 *
 * @param <T> the user define state type
 */
public interface StateMachine<T> extends WaitableSupplier<T>, WaitableNotify<T> {
    
    /**
     * Set the current state, state must already exist and be an allowed transition
     *
     * @param event the event name
     * @param state the new state
     * @return true if state was changed
     * @throws IllegalArgumentException when event is null, state is null, or unknown
     */
    boolean setState(String event, T state);
    
    /**
     * Get the current state
     *
     * @return the current state, never null
     */
    default T getState() {
        return get();
    }
    
    /**
     * Determine if the given state is known
     *
     * @param state the state to check
     * @return true iif the state is known
     * @throws IllegalArgumentException when state is null
     */
    boolean hasState(T state);
    
    /**
     * Determine if a transition is allowed from the current state to a new one.
     *
     * @param event the event that is triggering the transition
     * @param state the candidate state to transition to
     * @return if transition event is allowed to change the current state to the given state
     * @throws IllegalArgumentException when event is null, state is null, or unknown
     */
    boolean isTransitionAllowed(String event, T state);
    
    /**
     * Execute a transition from the current state to another
     *
     * @param builderConsumer the transition builder consumer
     * @param <B>             the transition return value
     * @param <R>             the return type of the transition. For example, a Closeable during open.
     * @return the builder consumer.
     * @throws IllegalArgumentException when builderConsumer is null or required fields are not present.
     */
    <B extends Transition.Builder<B, T, R>, R> R transition(Consumer<Transition.Builder<B, T, R>> builderConsumer);
    
    /**
     * Execute a transition from the current state to another
     *
     * @param transition the transition to execute
     * @param <R>        the return type of the transition. For example, a Closeable during open.
     * @return the transition return value
     * @throws IllegalArgumentException when transition is null or required fields are not present.
     */
    <R> R transition(Transition<T, R> transition);
    
    /**
     * Defines how a transition between states will be done
     *
     * @param <R> return type of the transition
     */
    interface Transition<S, R> {
        
        /**
         * @return the name of the event
         */
        String getEvent();
        
        /**
         * @return the success state of this transition
         */
        S getSuccessState();
        
        /**
         * @return the optional state if an exception is thrown
         */
        Optional<S> getErrorState();
        
        /**
         * @return the optional state if the transition is not allowed
         */
        Optional<S> getFailedState();
        
        /**
         * @return the optional return value on success
         */
        Optional<Supplier<R>> getSuccessValue();
        
        /**
         * @return the optional return value on exception thrown
         */
        Optional<Supplier<R>> getErrorValue();
        
        /**
         * @return the optional return value if transition is not allowed
         */
        Optional<Supplier<R>> getFailedValue();
   
        /**
         * Responsible for building a Transition
         *
         * @param <S> the return type of the transition
         */
        interface Builder<B extends Builder<B, S, R>, S, R> extends Transition<S, R> {
            
            /**
             * Assign the required event name
             *
             * @param event the event name
             * @return this builder
             */
            Builder<B, S, R> event(String event);
            
            /**
             * Assign the required success state
             *
             * @param state the success state
             * @return this builder
             */
            Builder<B, S, R> successState(S state);
            
            /**
             * Assign the optional success value
             *
             * @param valueSupplier the success value supplier
             * @return this builder
             */
            Builder<B, S, R> successValue(Supplier<R> valueSupplier);
            
            /**
             * Assign the optional success block
             *
             * @param runnable the runnable action
             * @return this builder
             */
            Builder<B, S, R> successValue(Runnable runnable);
            
            /**
             * Assign the optional error state used if there is exception
             *
             * @param state the error state
             * @return this builder
             */
            Builder<B, S, R> errorState(S state);
            
            /**
             * Assign the optional error value
             * Note: if this is set, when an exception occurs this value will be returned
             *
             * @param valueSupplier the error value supplier
             * @return this builder
             */
            Builder<B, S, R> errorValue(Supplier<R> valueSupplier);
            
            /**
             * Assign the optional failed state used if the goal state is not allowed
             *
             * @param state the failed state
             * @return this builder
             */
            Builder<B, S, R> failedState(S state);
            
            /**
             * Assign the optional failed value used when the goal state is not allowed
             *
             * @param valueSupplier the failed value supplier
             * @return this builder
             */
            Builder<B, S, R> failedValue(Supplier<R> valueSupplier);
        }
    }
    
    /**
     * Opt-in interface a state type can implement to assist in determining the valid transitions
     */
    interface Rule<T> {
        
        /**
         * Determine if 'this' state can transition to the target
         *
         * @param event the event name
         * @param goal  the goal state
         * @return true if the transition is valid
         */
        boolean canTransition(String event, T goal);
    }
    
    /**
     * StateMachine configuration
     *
     * @param <T> the type of each state
     */
    interface Config<T> {
        
        /**
         * Return the initial value. It is required, the use of required is because
         * the builder may not have provided a value
         *
         * @return the optional initial state
         */
        Optional<T> getInitial();
        
        /**
         * @return the list of states in the state machine
         */
        List<T> getStates();
        
        /**
         * Get all the rules for a specified state
         *
         * @param state the state
         * @return the rules of the state
         */
        List<Rule<T>> getStateRules(T state);
        
        /**
         * The Builder for a State Machine
         * @param <T> the type of each state
         */
        interface Builder<T> extends Config<T> {
            
            /**
             * Assign the required initial state
             * @param state the initial state
             * @return this builder
             */
            Builder<T> initial(T state);
            
            /**
             * Add a new state
             * @param state the state to add
             * @return this builder
             */
            Builder<T> state(T state);
            
            /**
             * Add a list of states
             * @param states the states to add
             * @return this builder
             */
            Builder<T> states(List<T> states);
            
            /**
             * Add a rule to a state
             *
             * @param state the state to add rules to
             * @param rule the rule to add
             * @return this builder
             */
            Builder<T> rule(T state, Rule<T> rule);
            
            /**
             * Add many rules to a state
             * @param state the to add rules to
             * @param rules the rules to add
             * @return this builder
             */
            Builder<T> rules(T state, List<Rule<T>> rules);
        }
    }
}
