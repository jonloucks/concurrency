package io.github.jonloucks.concurrency.api;

import java.util.function.Consumer;

/**
 * State machine, containing a set of sets that can be transitioned to by events/actions
 * @param <T> the user define state type
 */
public interface StateMachine<T> {
    
    /**
     * Set the current state, state must already exist and be an allowed transition
     * @param state the new state
     * @return true if state was changed
     * @throws IllegalArgumentException when event is null, state is null, or unknown
     */
    boolean setState(String event, T state);
    
    /**
     * Get the current state
     * @return the current state, never null
     */
    T getState();
    
    /**
     * Adds a new state if it does not already exist
     * @param state the new state
     * @throws IllegalArgumentException when state is null
     */
    void addState(T state);
    
    /**
     * Determine if the given state is known
     * @param state the state to check
     * @return true iif the state is known
     * @throws IllegalArgumentException when state is null
     */
    boolean hasState(T state);
    
    /**
     * Determine if a transition is allowed from the current state to a new one.
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
     * @return the builder consumer.
     * @param <B> the transition return value
     * @param <R> the return type of the transition. For example, a Closeable during open.
     *  @throws IllegalArgumentException when builderConsumer is null or required fields are not present.
     */
    <B extends Transition.Builder<B, T, R>, R> R transition(Consumer<Transition.Builder<B, T, R>> builderConsumer);
    
    /**
     * Execute a transition from the current state to another
     *
     * @param transition the transition to execute
     * @return the transition return value
     * @param <R> the return type of the transition. For example, a Closeable during open.
     * @throws IllegalArgumentException when transition is null or required fields are not present.
     */
    <R> R transition(Transition<T, R> transition);
    
//    interface Config<T> {
//        T initialState();
//
//        interface Builder<B extends Builder<B, T>, T> {
//            Builder<B, T> initialState(T initialState);
//        }
//    }
}
