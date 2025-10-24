package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

final class Internal {
    
    static Stream<Arguments> getThrowingParameters() {
        return Stream.of(
            Arguments.of((Runnable) () -> {
                throw new Error("Error.");
            }),
            Arguments.of((Runnable) () -> {
                throw new RuntimeException("RuntimeException.");
            }),
            Arguments.of((Runnable) () -> {
                throw new ConcurrencyException("ConcurrencyException.");
            })
        );
    }
    
    /**
     * Utility class instantiation protection
     * Test coverage not possible, java module protections in place
     */
    private Internal() {
        // conflicting standards.  100% code coverage vs throwing exception on instantiation of utility class.
        // Java modules protects agents invoking private methods.
        // There are unit tests that will fail if this constructor is not private
    }
}
