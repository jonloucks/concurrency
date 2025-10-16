package io.github.jonloucks.concurrency.api.test;

import io.github.jonloucks.concurrency.api.GlobalConcurrency;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;

public interface InternalTests {
    
    @Test
    default void api_InstantiateGlobalConcurrency_Throws() {
        assertInstantiateThrows(GlobalConcurrency.class);
    }
}
