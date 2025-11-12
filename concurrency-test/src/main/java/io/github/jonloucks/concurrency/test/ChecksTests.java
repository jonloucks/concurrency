package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Checks;
import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.concurrency.api.StateMachine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.assertSame;

@SuppressWarnings("ResultOfMethodCallIgnored")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface ChecksTests {
    
    @Test
    default void checks_Instantiate_Throws() {
        assertInstantiateThrows(Checks.class);
    }
 
    @Test
    default void checks_concurrencyCheck_WhenNull_Throws() {
        assertThrown(IllegalArgumentException.class, () -> Checks.concurrencyCheck(null));
    }

    @Test
    default void checks_onCompletionCheck_WhenNull_Throws() {
        assertThrown(IllegalArgumentException.class, () -> Checks.onCompletionCheck(null));
    }
    
    @Test
    default void checks_stateMachineCheck_WhenNull_Throws() {
        assertThrown(IllegalArgumentException.class, () -> Checks.stateMachineCheck(null));
    }
    
    @Test
    default void checks_concurrencyCheck_WithValid_Works(@Mock Concurrency concurrency) {
        assertSame(concurrency, Checks.concurrencyCheck(concurrency));
    }
    
    @Test
    default void checks_onCompletionCheck_WithValid_Works(@Mock OnCompletion<String> onCompletion) {
        assertSame(onCompletion, Checks.onCompletionCheck(onCompletion));
    }
    
    @Test
    default void checks_stateMachineCheck_WithValid_Works(@Mock StateMachine<String> stateMachine) {
        assertSame(stateMachine, Checks.stateMachineCheck(stateMachine));
    }
    
}
