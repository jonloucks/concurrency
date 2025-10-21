package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contracts;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * Idempotent state machine states
 */
public enum Idempotent implements StateMachine.Rule<Idempotent> {
    /**
     * Initial state
     * OPENABLE can transition to OPENED, OPENING, CLOSED, or DESTROYED.
     */
    OPENABLE {
        @Override
        public boolean canTransition(String event, Idempotent goal) {
            return goal == OPENED || goal == OPENING || goal == CLOSED || goal == DESTROYED;
        }
    },
    /**
     * For use cases where the open can take some time or reentrancy calls while initializing.
     * Transitioning to OPENING and then OPENED will allow requests to not be rejected.
     * OPENING can transition to OPENED, CLOSED, OPENABLE, DESTROYED.
     */
    OPENING {
        @Override
        public boolean canTransition(String event, Idempotent goal) {
            return goal == OPENED || goal == CLOSED || goal == OPENABLE || goal == DESTROYED;
        }
        @Override
        public boolean isRejecting() {
            return false;
        }
    },
    /**
     * Resource, service is open to actions
     * OPEN can transition to CLOSING or CLOSED.
     */
    OPENED {
        @Override
        public boolean canTransition(String event, Idempotent goal) {
            return goal == CLOSING || goal == CLOSED;
        }
        @Override
        public boolean isRejecting() {
            return false;
        }
    },
    /**
     * A state of closing or shutting down.
     * Note: isRejecting returns false during while close, but implementation can decide
     * if a new action will be processed.
     * CLOSING can transition to CLOSED or DESTROYED
     */
    CLOSING {
        @Override
        public boolean canTransition(String event, Idempotent goal) {
            return goal == CLOSED || goal == DESTROYED;
        }
    },
    /**
     * A state that all new actions are invalid
     * Implementations can decide to ignore or throw exception, but never be processed.
     * CLOSED can transition to OPENABLE or DESTROYED
     */
    CLOSED {
        @Override
        public boolean canTransition(String event, Idempotent goal) {
            return goal == OPENABLE || goal == DESTROYED;
        }
    },
    /**
     * Represents a permanent end state.
     * Implementations can decide to ignore or throw exception, but never be processed.
     * DESTROYED can not transition
     */
    DESTROYED;
    
    @Override
    public boolean canTransition(String event, Idempotent goal) {
        return false;
    }
    
    /**
     * Determines if new requests/action should be rejected
     *
     * @return true if requests should be rejected.
     * How they are rejected is an implementation decision.
     * A request could be ignored, cause an exception, or be processed differently
     */
    public boolean isRejecting() {
        return true;
    }
    
    /**
     * Create a StateMachine for Idempotency
     * @param contracts the contracts for getting dependencies
     * @return the new StateMachine
     * @throws IllegalArgumentException if contracts is null
     */
    public static StateMachine<Idempotent> createStateMachine(Contracts contracts) {
        final Contracts validContracts = contractsCheck(contracts);
        final StateMachineFactory factory = validContracts.claim(StateMachineFactory.CONTRACT);
        return factory.create( b -> {
            b.initial(OPENABLE);
            for (Idempotent idempotent : Idempotent.values()) {
                b.state(idempotent);
                b.rule(idempotent, idempotent);
            }
        });
    }
    
    /**
     * Assist with an idempotent close
     *
     * @param machine the StateMachine
     * @param close the close
     */
    public static void withClose(StateMachine<Idempotent> machine, AutoClose close) {
        final StateMachine<Idempotent> validMachine = nullCheck(machine, "State machine must be present.");
        final AutoClose validClose = nullCheck(close, "Close must be present.");
        
        validMachine.transition(b -> b
            .event("close")
            .successState(Idempotent.CLOSED)
            .successValue(() -> validClose.close())
            .failedValue(() -> Void.TYPE)
            .errorValue(() -> Void.TYPE));
    }
    
    /**
     * Assist with an idempotent close
     * @param machine the state machine
     * @param open the open
     * @return the AutoClose
     */
    public static AutoClose withOpen(StateMachine<Idempotent> machine, AutoOpen open) {
        final StateMachine<Idempotent> validMachine = nullCheck(machine, "State machine must be present.");
        final AutoOpen validOpen = nullCheck(open, "Open must be present.");
        
        return validMachine.transition(b -> b
            .event("open")
            .successState(Idempotent.OPENED)
            .successValue(validOpen::open)
            .failedValue(() -> AutoClose.NONE)
        );
    }
}
