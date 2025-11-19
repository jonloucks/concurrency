package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contracts;

import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;

/**
 * Responsibility: Represent a progression step in the life cycle of an activity.
 */
@FunctionalInterface
public interface Completion<T> {
    
    /**
     * @return the completion state
     */
    State getState();
    
    /**
     * @return optional thrown exception
     */
    default Optional<Throwable> getThrown() {
        return Optional.empty();
    }
    
    /**
     * @return the optional completion value
     */
    default Optional<T> getValue() {
        return Optional.empty();
    }
    
    /**
     * @return the optional associated Future
     */
    default Optional<Future<T>> getFuture() {
        return Optional.empty();
    }
    
    /**
     * True if final completion
     * @return true if final completion
     */
    default boolean isCompleted() {
        return getState().isCompleted();
    }
    
    /**
     * Configuration for creating a new Completion
     * @param <T> the value type
     */
    interface Config<T> extends Completion<T> {
        
        /**
         * Builder for creating a configuration for a new Completion
         *
         * @param <T> the value type
         */
        interface Builder<T> extends Config<T> {
            
            /**
             * Assign the state
             *
             * @param state the new state
             * @return this builder
             */
            Builder<T> state(State state);
            
            /**
             * Assign the thrown exception
             *
             * @param thrown the exception
             * @return this builder
             */
            Builder<T> thrown(Throwable thrown);
            
            /**
             * Assign the value
             *
             * @param value the value
             * @return this builder
             */
            Builder<T> value(T value);
            
            /**
             * Assign the future
             *
             * @param future the future
             * @return this builder
             */
            Builder<T> future(Future<T> future);
        }
    }
    
    /**
     * The Completion states
     */
    enum State implements StateMachine.Rule<State> {
        /**
         * The initial state
         */
        PENDING() {
            @Override
            public boolean canTransition(String event, State goal) {
                return goal == FAILED || goal == CANCELLED || goal == SUCCEEDED;
            }
        },
        /**
         * Represents a failed completion
         */
        FAILED() {
            @Override
            public boolean isCompleted() {
                return true;
            }
        },
        /**
         * Represents a canceled completion
         */
        CANCELLED() {
            @Override
            public boolean isCompleted() {
                return true;
            }
        },
        /**
         * Represents a successful completion
         */
        SUCCEEDED() {
            @Override
            public boolean isCompleted() {
                return true;
            }
        };
        
        @Override
        public boolean canTransition(String event, State goal) {
            return false;
        }
        
        /**
         * @return true of state is a completed state.
         */
        public boolean isCompleted() {
            return false;
        }
        
        /**
         * Create a StateMachine for Completion State
         *
         * @param contracts the contracts for getting dependencies
         * @return the new StateMachine
         * @throws IllegalArgumentException if contracts is null
         */
        public static StateMachine<State> createStateMachine(Contracts contracts) {
            final Contracts validContracts = contractsCheck(contracts);
            final StateMachineFactory factory = validContracts.claim(StateMachineFactory.CONTRACT);
            return factory.create(b -> {
                b.initial(PENDING);
                for (State outcome : State.values()) {
                    b.state(outcome);
                    b.rule(outcome, outcome);
                }
            });
        }
    }
}
