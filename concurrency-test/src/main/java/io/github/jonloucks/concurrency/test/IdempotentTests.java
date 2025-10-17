package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.Idempotent.State;
import io.github.jonloucks.concurrency.api.Idempotent.Transition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static io.github.jonloucks.concurrency.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
public interface IdempotentTests {
    
    @Test
    default void idempotent_transitions() {
        withConcurrency(b ->{}, (contracts, concurrency) -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            assertEquals(State.INITIAL, idempotent.getState());
            assertFalse(State.INITIAL.canTransitionTo(State.CLOSED));
            assertTrue(State.INITIAL.canTransitionTo(State.OPENING));
            assertTrue(State.OPENING.canTransitionTo(State.OPENED));
            assertTrue(State.OPENING.canTransitionTo(State.INITIAL));
        });
    }
    
    @Test
    default void idempotent_isRejecting() {
        assertTrue(State.INITIAL.isRejecting());
        assertFalse(State.OPENING.isRejecting());
        assertFalse(State.OPENED.isRejecting());
        assertFalse(State.CLOSING.isRejecting());
        assertTrue(State.CLOSED.isRejecting());
    }
    
    @ParameterizedTest
    @EnumSource(State.class)
    default void idempotent_state_attemptTransition_Self_IsFalse(State state) {
        assertFalse(state.canTransitionTo(state));
    }
    
    @Test
    default void idempotent_transition_WithNullState_Throws() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                idempotent.transition((State)null);
            });
            assertThrown(thrown );
        });
    }
    
    @Test
    default void idempotent_transition_WithNullBuilderConsumer_Throws() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                idempotent.transition((Consumer<Transition.Builder<String>>)null);
            });
            assertThrown(thrown );
        });
    }
    
    @Test
    default void idempotent_transition_WithNullTransition_Throws() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                idempotent.transition((Transition<String>)null);
            });
            assertThrown(thrown );
        });
    }
    
    @Test
    default void idempotent_transition_WithNoGoalState_Throws() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                idempotent.transition(b->{});
            });
            assertThrown(thrown );
        });
    }
    
    @Test
    default void idempotent_transition_WithIllegalTransition_Throws() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                idempotent.transition(b -> {
                    b.interimState(State.CLOSING)
                        .goalState(State.OPENING);
                });
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void idempotent_transition_alwaysWithException_ChangesState() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final Error expected = new Error("Error.");
            final Error thrown = assertThrows(Error.class, () -> {
                idempotent.transition(b -> b
                    .goalState(State.OPENING)
                    .action(() -> { throw expected; })
                    .always(true)
                );
            });
            assertEquals(expected, thrown);
            assertEquals(State.OPENING, idempotent.getState());
        });
    }
    
    @Test
    default void idempotent_transition_WithException_RevertsState() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final Error expected = new Error("Error.");
            final Error thrown = assertThrows(Error.class, () -> {
                idempotent.transition(b -> b
                    .goalState(State.OPENING)
                    .action(() -> { throw expected; })
                    .always(false)
                );
            });
            assertEquals(expected, thrown);
            assertEquals(State.INITIAL, idempotent.getState());
        });
    }
    
    @Test
    default void idempotent_transition_InterimFails_Else_Returns() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final String text = idempotent.transition(b -> b
                .interimState(State.CLOSING)
                .goalState(State.CLOSED)
                .orElse(()->"else")
            );
            assertEquals("else", text);
            assertEquals(State.INITIAL, idempotent.getState());
        });
    }
    
    @Test
    default void idempotent_transition_NullActions_Works() {
        withConcurrencyInstalled(contracts -> {
            final Idempotent idempotent = contracts.claim(Idempotent.FACTORY).get();
            final String text = idempotent.transition(b -> b
                .action((Runnable)null)
                .action((Supplier<String>)null)
                .goalState(State.OPENING)
            );
            assertEquals(State.OPENING, idempotent.getState());
            assertNull(text);
        });
    }
}
