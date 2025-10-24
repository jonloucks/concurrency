package io.github.jonloucks.concurrency.test;

import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;

public interface InternalTests extends
    ToolsTests {
    
    @Test
    default void internal_Instantiate_Throws() {
        assertInstantiateThrows(Internal.class);
    }
}
