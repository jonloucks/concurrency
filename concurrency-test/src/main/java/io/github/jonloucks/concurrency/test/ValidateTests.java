package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Checks;
import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.contracts.api.GlobalContracts;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("CodeBlock2Expr")
public interface ValidateTests {
    
    @Test
    default void validate_WithNullContracts_Throws() {
        withConcurrency((contracts, concurrency) -> {
            assertThrown(IllegalArgumentException.class,
                () -> Checks.validateConcurrency(null, concurrency),
                "Contracts must be present.");
        });
    }
    
    @Test
    default void validate_WithNullConcurrency_Throws() {
        withConcurrency((contracts, concurrency) -> {
            assertThrown(IllegalArgumentException.class,
                () -> Checks.validateConcurrency(contracts, null),
                "Concurrency must be present.");
        });
    }
    
    @Test
    default void validate_WithConcurrencyClaimDifferent_Throws() {
        withConcurrency((contracts, concurrency) -> {
            assertThrown(ConcurrencyException.class,
                () -> Checks.validateConcurrency(GlobalContracts.getInstance(), concurrency),
                "Concurrency.CONTRACT claim is different.");
        });
    }
    
    @Test
    default void validate_Valid_Works() {
        withConcurrency((contracts, concurrency) -> {
            assertDoesNotThrow(() -> Checks.validateConcurrency(contracts, concurrency));
        });
    }
}
