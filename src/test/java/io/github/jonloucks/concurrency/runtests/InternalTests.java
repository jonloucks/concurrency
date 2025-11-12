package io.github.jonloucks.concurrency.runtests;

import io.github.jonloucks.concurrency.Stub;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public interface InternalTests {
    
    @Test
    default void internal_stub_Instantiate_Throws() {
        assertInstantiateThrows(Stub.class);
    }
    
    @Test
    default void internal_stub_validate() {
        assertDoesNotThrow(() -> Stub.validate());
    }
    
    @Test
    default void internal_stub_validate_Throws() {
        final Contracts contracts = GlobalContracts.getInstance();
        assertThrown(IllegalArgumentException.class,
            () -> Stub.validate(contracts, null));
    }
}
