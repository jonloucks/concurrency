package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contracts;

import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;

/**
 * Responsibility: Represent a progression step in the life cycle of an activity.
 */
public interface Completion<T> {
    
    State getState();
    
    Optional<Throwable> getThrown();
    
    Optional<T> getValue();
    
    Optional<Future<T>> getFuture();
    
    default boolean isCompleted() {
        return getState().isCompleted();
    }
    
    interface Config<T> extends Completion<T> {
        interface Builder<T> extends Config<T> {
            Builder<T> state(State state);
            
            Builder<T> thrown(Throwable thrown);
            
            Builder<T> value(T value);
            
            Builder<T> future(Future<T> future);
        }
    }
    
    enum State implements StateMachine.Rule<State> {
        NEW() {
            @Override
            public boolean canTransition(String event, State goal) {
                return goal == DELEGATED || goal == FAILED || goal == CANCELLED || goal == SUCCEEDED;
            }
        },
        DELEGATED() {
            @Override
            public boolean canTransition(String event, State goal) {
                return goal == FAILED || goal == CANCELLED || goal == SUCCEEDED;
            }
        },
        FAILED() {
            @Override
            public boolean isCompleted() {
                return true;
            }
        },
        CANCELLED() {
            @Override
            public boolean isCompleted() {
                return true;
            }
        },
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
                b.initial(NEW);
                for (State outcome : State.values()) {
                    b.state(outcome);
                    b.rule(outcome, outcome);
                }
            });
        }
    }
}
