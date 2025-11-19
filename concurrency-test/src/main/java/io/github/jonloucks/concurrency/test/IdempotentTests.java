package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.concurrency.api.Idempotent;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static io.github.jonloucks.concurrency.api.Idempotent.*;
import static io.github.jonloucks.concurrency.test.Tools.*;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static io.github.jonloucks.contracts.test.Tools.ignore;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface IdempotentTests {
    
    @Test
    default void idempotent_transitions() {
        assertTransitions(Idempotent.class, OPENABLE, asList(OPENED, OPENING, CLOSED, DESTROYED));
        assertTransitions(Idempotent.class, OPENING, asList(OPENED, CLOSED, OPENABLE, DESTROYED));
        assertTransitions(Idempotent.class, OPENED, asList(CLOSING, CLOSED));
        assertTransitions(Idempotent.class, CLOSING, asList(CLOSED, DESTROYED));
        assertTransitions(Idempotent.class, CLOSED, asList(OPENABLE, DESTROYED));
        assertTransitions(Idempotent.class, DESTROYED, emptyList());
    }
    
    @Test
    default void idempotent_isRejecting() {
        assertTrue(OPENABLE.isRejecting());
        assertFalse(OPENING.isRejecting());
        assertFalse(OPENED.isRejecting());
        assertTrue(CLOSING.isRejecting());
        assertTrue(CLOSED.isRejecting());
        assertTrue(DESTROYED.isRejecting());
    }
    
    @Test
    default void idempotent_createStateMachine_WithNullContracts_Throws() {
        assertThrown(IllegalArgumentException.class,
            () -> Idempotent.createStateMachine(null),
            "Contracts must be present.");
    }
    
    @Test
    default void idempotent_transition_WithIllegalTransition_Throws() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            assertThrown(ConcurrencyException.class,
                () -> stateMachine.transition(b -> b
                .event("close")
                .successState(CLOSING)
                .successValue(() -> "success value")
            ));
        });
    }
    
    @Test
    default void idempotent_transition_WithIllegalTransitionAndFailedValue_Works() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            final String value = stateMachine.transition(b -> b
                .event("close")
                .successState(CLOSING)
                .successValue(() -> "success value")
                .errorValue(() -> "error value")
                .failedValue(() -> "failed value")
            );
            assertEquals("failed value", value);
        });
    }
    
    @Test
    default void idempotent_transition_WithIllegalTransitionAndFailedState_Works() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            final String value = stateMachine.transition(b -> b
                .event("close")
                .successState(CLOSING)
                .successValue(() -> "success value")
                .errorValue(() -> "error value")
                .failedState(DESTROYED)
                .failedValue(() -> "failed value")
            );
            assertEquals(DESTROYED, stateMachine.getState());
            assertEquals("failed value", value);
        });
    }
    
    @Test
    default void idempotent_transition_ErrorStateWithException_ChangesState() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            final Error expected = new Error("Error.");
            final Error thrown = assertThrows(Error.class, () -> {
                stateMachine.transition(b -> b
                    .event("start opening")
                    .successState(OPENING)
                    .successValue(() -> { throw expected; })
                    .errorState(DESTROYED)
                );
            });
            assertEquals(expected, thrown);
            assertEquals(DESTROYED, stateMachine.getState());
        });
    }
    
    @Test
    default void idempotent_transition_ErrorStateWithExceptionAndNoRethrow_Works() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            final Error expected = new Error("Error.");
            
            final String value = stateMachine.transition(b -> b
                    .event("start opening")
                    .successState(OPENING)
                    .successValue(() -> { throw expected; })
                    .errorValue(() -> "error value")
                    .failedValue(() -> "failed value")
                    .errorState(DESTROYED)
                );
    
            assertEquals("error value", value);
        });
    }
    
    @Test
    default void idempotent_withOpen_WithNullMachine_Throws() {
        withConcurrencyInstalled(contracts -> {
            assertThrown(IllegalArgumentException.class, () -> {
                //noinspection resource
                Idempotent.withOpen(null, AutoOpen.NONE);
            }, "StateMachine must be present.");
        });
    }
    
    @Test
    default void idempotent_withOpen_WithNullOpen_Throws() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            assertThrown(IllegalArgumentException.class, () -> {
                    //noinspection resource
                    Idempotent.withOpen(stateMachine, null);
                },
                "Open must be present.");
        });
    }
    
    @Test
    default void idempotent_withClose_WithNullMachine_Throws() {
        withConcurrencyInstalled(contracts -> {
            assertThrown(IllegalArgumentException.class,
                () -> Idempotent.withClose(null, AutoClose.NONE),
                "StateMachine must be present.");
        });
    }
    
    @Test
    default void idempotent_withClose_WithNullClose_Throws() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            assertThrown(IllegalArgumentException.class, () -> {
                Idempotent.withClose(stateMachine, null);
            },
                "Close must be present.");
        });
    }
    
    @Test
    default void idempotent_withClose_WithValid_Works(@Mock AutoClose close) {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            Idempotent.withClose(stateMachine, close);
            
            verify(close, times(1)).close();
        });
    }
    
    @Test
    default void idempotent_withClose_WithError_IsIgnored() {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            assertDoesNotThrow(() -> {
                Idempotent.withClose(stateMachine, () -> {throw new Error("Error.");});
                Idempotent.withClose(stateMachine, () -> {throw new RuntimeException("RuntimeException.");});
                Idempotent.withClose(stateMachine, () -> {throw new ConcurrencyException("ConcurrencyException.");});
            });
        });
    }
    
    @Test
    default void idempotent_withClose_WithValid_Works(@Mock AutoClose close, @Mock AutoClose close2) {
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            Idempotent.withClose(stateMachine, close);
            Idempotent.withClose(stateMachine, close2);
            
            verify(close, times(1)).close();
            verify(close2, times(0)).close();
        });
    }
    
    @Test
    default void idempotent_withOpen_WithValid_Works(@Mock AutoOpen open) {
        when(open.open()).thenReturn(AutoClose.NONE);
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            final AutoClose close = Idempotent.withOpen(stateMachine, open);
            
            assertNotNull(close);
            //noinspection resource
            verify(open, times(1)).open();
        });
    }
    
    @Test
    default void idempotent_withOpen_WithValidTwice_Works(@Mock AutoOpen open, @Mock AutoOpen open2) {
        when(open.open()).thenReturn(AutoClose.NONE);
        when(open2.open()).thenReturn(AutoClose.NONE);
        
        withConcurrencyInstalled(contracts -> {
            final StateMachine<Idempotent> stateMachine = Idempotent.createStateMachine(contracts);
            
            try (AutoClose close = Idempotent.withOpen(stateMachine, open);
                AutoClose close2 = Idempotent.withOpen(stateMachine, open2)) {
                ignore(close);
                ignore(close2);
            }

            //noinspection resource
            verify(open, times(1)).open();
            //noinspection resource
            verify(open2, times(0)).open();
        });
    }
}
