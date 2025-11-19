package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Completion.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import static io.github.jonloucks.concurrency.api.Completion.State.*;

import static io.github.jonloucks.concurrency.test.Tools.assertTransitions;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface CompletionStateTests {
    
    @Test
    default void completionState_Transitions() {
        assertTransitions(State.class, PENDING, asList(SUCCEEDED,FAILED,CANCELLED));
        assertTransitions(State.class, SUCCEEDED, emptyList());
        assertTransitions(State.class, FAILED, emptyList());
        assertTransitions(State.class, CANCELLED, emptyList());
    }
    
    @Test
    default void completionState_IsCompleted() {
        assertFalse(PENDING.isCompleted());
        assertTrue(SUCCEEDED.isCompleted());
        assertTrue(FAILED.isCompleted());
        assertTrue(CANCELLED.isCompleted());
    }
    
    @Test
    default void completionState_IsFailed() {
        assertThrown(IllegalArgumentException.class,
            () -> State.createStateMachine(null));
    }
}
