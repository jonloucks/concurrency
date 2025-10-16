package io.github.jonloucks.concurrency.impl.test;

import io.github.jonloucks.concurrency.impl.Stub;
import org.junit.jupiter.api.Test;

public interface InternalTests {
    
    @Test
    default void internalTests() {
        Stub.validate();
    }
}
