package io.github.jonloucks.concurrency.api;

/**
 * Opt-in interface a state type can implement to assist in determining the valid transitions
 */
public interface TransitionAware {
    
    /**
     * Determine if 'this' state can transition to the candidateState.
     *
     * @param event the event name
     * @param candidateState the candidate state
     * @return true if the transition is valid
     */
    boolean canTransitionTo(String event, Object candidateState);
}
