package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Contract;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Idempotent.State.*;

/**
 * Responsible for managing open/closed state
 */
public interface Idempotent {
    
    /**
     * The factory contract for creating a new Idempotent
     */
    Contract<Supplier<Idempotent>> FACTORY = Contract.create("Idempotent Factory");
    
    /**
     * @return Get the current state
     */
    State getState();
    
    /**
     * Transition to a new state
     * Note: Transitioning to the current state will return false
     *
     * @param state the new state
     * @return true iif the current state allows the transition to the new state
     */
    boolean transition(State state);
    
    /**
     * Transition using Transition Builder consumer
     * @param builderConsumer the builder consumer.
     * @return the return type of the transition. For example, a Closeable during open.
     * @param <T> return type of the transition
     */
    <T> T transition(Consumer<Transition.Builder<T>> builderConsumer);
    
    /**
     * Transition using a Transition
     * @param transition the transition
     * @return the return type of the transition. For example, a Closeable during open.
     * @param <T> return type of the transition
     */
    <T> T transition(Transition<T> transition);
    
    default void transitionToClosed(Runnable action) {
        transition(b -> b
            .interimState(CLOSING)
            .goalState(CLOSED)
            .always(true)
            .action(action)
        );
    }
    
    default AutoClose transitionToOpened(Supplier<AutoClose> opener) {
        return transition(b -> b
            .interimState(OPENING)
            .goalState(OPENED)
            .action(opener)
            .orElse(() -> AutoClose.NONE)
        );
    }
    
    /**
     * The possible states
     */
    enum State {
        INITIAL {
            @Override
            public boolean canTransitionTo(State state) {
                return OPENING == state;
            }
        },
        OPENING {
            @Override
            public boolean canTransitionTo(State state) {
                return OPENED == state || INITIAL == state;
            }
            
            @Override
            public boolean isRejecting() {
                return false;
            }
        },
        OPENED() {
            @Override
            public boolean canTransitionTo(State state) {
                return CLOSING == state;
            }
            
            @Override
            public boolean isRejecting() {
                return false;
            }
        },
        CLOSING {
            @Override
            public boolean canTransitionTo(State state) {
                return CLOSED == state;
            }
            
            @Override
            public boolean isRejecting() {
                return false;
            }
        },
        CLOSED;
        
        public boolean isRejecting() {
            return true;
        }
        
        public boolean canTransitionTo(State state) {
            return false;
        }
        
        State() {
        }
    }
    
    /**
     * Defines how a transition between states will be done
     * @param <T> return type of the transition
     */
    interface Transition<T> {
        Optional<State> interimState();
        
        Optional<State> goalState();
        
        Optional<Supplier<T>> action();
        
        Optional<Supplier<T>> orElse();
        
        boolean always();
        
        /**
         * Responsible for building a Transition
         * @param <T> the return type of the transition
         */
        interface Builder<T> extends Transition<T> {
            Builder<T> interimState(State stepState);
            
            Builder<T> goalState(State goalState);
            
            Builder<T> action(Supplier<T> action);
            
            Builder<T> action(Runnable action);
            
            Builder<T> orElse(Supplier<T> orElse);
            
            Builder<T> always(boolean always);
        }
    }
}
