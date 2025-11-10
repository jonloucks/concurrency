package io.github.jonloucks.concurrency.runtests;

import io.github.jonloucks.concurrency.Stub;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public interface InternalTests {
    
    @Test
    default void stub_Instantiate_Throws() {
        assertInstantiateThrows(Stub.class);
    }
    
    @Test
    default void stub_validate() {
        assertDoesNotThrow(() -> Stub.validate());
    }
    
    @Test
    default void stub_validate_args() {
        withConcurrency((contracts,concurrency) ->
            assertDoesNotThrow(() -> Stub.validate(contracts, concurrency)));
    }
}
