package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.Waitable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.assertObject;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface ConcurrencyTests {
    
    @Test
    default void concurrency_isValidObject() {
        withConcurrency((contracts, concurrency) -> {
            assertObject(concurrency);
        });
    }
    
    @Test
    default void concurrency_createWaitable_WithValidInitial_Works() {
        withConcurrency((contracts, concurrency) -> {
            final Waitable<String> stateMachine = concurrency.createWaitable("hello");
            assertObject(stateMachine);
            assertEquals("hello", stateMachine.get());
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithNullInitialState_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createStateMachine(null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithValidInitial_Works() {
        withConcurrency((contracts, concurrency) -> {
            final String initialState = "initialState";
            final StateMachine<String> stateMachine = concurrency.createStateMachine(initialState);
            assertObject(stateMachine);
            assertEquals(initialState, stateMachine.getState());
            assertEquals(initialState, stateMachine.get());
            assertTrue(stateMachine.getIf(initialState::equals).isPresent());
            assertEquals(initialState, stateMachine.getIf(initialState::equals).get());
            assertTrue(stateMachine.getWhen(initialState::equals).isPresent());
            assertEquals(initialState, stateMachine.getWhen(initialState::equals).get());
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithNullEnumClass_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createStateMachine(null, Thread.State.NEW);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithEnumClassAndNullInitialState_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createStateMachine((Consumer<StateMachine.Config.Builder<String>>)null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithNullBuilderConsumer_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createStateMachine(Thread.State.class, null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithValidEnum_Works() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachine<Thread.State> stateMachine = concurrency.createStateMachine(Thread.State.class, Thread.State.RUNNABLE);
            assertObject(stateMachine);
            assertEquals(Thread.State.RUNNABLE, stateMachine.getState());
        });
    }
    
    @Test
    default void concurrency_createStateMachine_WithValidBuilderConsumer_Works() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachine<Thread.State> stateMachine = concurrency.createStateMachine(b ->
                b.initial(Thread.State.RUNNABLE)
            );
            assertObject(stateMachine);
            assertEquals(Thread.State.RUNNABLE, stateMachine.getState());
        });
    }
}
