package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Idempotent;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.jonloucks.concurrency.api.Idempotent.*;
import static io.github.jonloucks.concurrency.test.IdempotentTests.IdempotentTestsTools.assertTransitions;
import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.concurrency.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
public interface IdempotentTests {
    
    @Test
    default void idempotent_transitions() {
        assertTransitions(NEW, OPENED, OPENING, CLOSED, DESTROYED);
        assertTransitions(OPENING, OPENED, CLOSED, DESTROYED);
        assertTransitions(OPENED, CLOSING, CLOSED);
        assertTransitions(CLOSING, CLOSED, DESTROYED);
        assertTransitions(CLOSED, OPENED, OPENING, DESTROYED);
        assertTransitions(DESTROYED);
    }
    
    @Test
    default void idempotent_isRejecting() {
        assertTrue(NEW.isRejecting());
        assertFalse(OPENING.isRejecting());
        assertFalse(OPENED.isRejecting());
        assertTrue(CLOSING.isRejecting());
        assertTrue(CLOSED.isRejecting());
        assertTrue(DESTROYED.isRejecting());
    }

    
    @Test
    default void idempotent_transition_WithIllegalTransition_Throws() {
        withConcurrencyInstalled(contracts -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Idempotent> idempotent = factory.create(Idempotent.class, NEW);
            
            final String value = idempotent.transition(b -> b
                .event("close")
                .goalState(CLOSING)
                .action(() -> "this value should not be returned")
            );
            assertNull(value);
        });
    }
    
    @Test
    default void idempotent_transition_ErrorStateWithException_ChangesState() {
        withConcurrencyInstalled(contracts -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Idempotent> idempotent = factory.create(Idempotent.class, NEW);
            final Error expected = new Error("Error.");
            final Error thrown = assertThrows(Error.class, () -> {
                idempotent.transition(b -> b
                    .event("start opening")
                    .goalState(OPENING)
                    .action(() -> { throw expected; })
                    .errorState(DESTROYED)
                );
            });
            assertEquals(expected, thrown);
            assertEquals(DESTROYED, idempotent.getState());
        });
    }
    
    @Test
    default void idempotent_transition_ErrorStateWithExceptionAndNoRethrow_Works() {
        withConcurrencyInstalled(contracts -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Idempotent> idempotent = factory.create(Idempotent.class, NEW);
            final Error expected = new Error("Error.");
            final String value = idempotent.transition(b -> b
                    .event("start opening")
                    .goalState(OPENING)
                    .action(() -> { throw expected; })
                    .rethrow(false)
                    .orElse(() -> "else")
                    .errorState(DESTROYED)
                );
    
            assertEquals("else", value);
        });
    }

    @Test
    default void idempotent_InternalTools_Throws() {
        assertInstantiateThrows(Tools.class);
    }
    
    final class IdempotentTestsTools {
        
        static void assertTransitions(Idempotent from, Idempotent ... allowed) {
            assertDoesNotThrow(() -> { from.canTransitionTo("unknown", 1);});
            final List<Idempotent> allowList = Arrays.asList(allowed);
            for (Idempotent to : Idempotent.values()) {
                if (allowList.contains(to)) {
                    assertTrue(from.canTransitionTo("unnamed", to), "Expected transition to " + to);
                } else {
                    assertFalse(from.canTransitionTo("unnamed", to), "Unexpected transition to " + to);
                }
            }
        }
        private IdempotentTestsTools() {
        
        }
    }
}
