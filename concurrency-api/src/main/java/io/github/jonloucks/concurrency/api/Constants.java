package io.github.jonloucks.concurrency.api;

import java.time.Duration;

/**
 * Concurrency constants
 */
public final class Constants {
    /**
     * Utility class instantiation protection
     * Test coverage not possible, java module protections in place
     */
    private Constants() {
        // conflicting standards.  100% code coverage vs throwing exception on instantiation of utility class.
        // Java modules protects agents invoking private methods.
        // There are unit tests that will fail if this constructor is not private
    }
    
    /**
     * The minimum timeout duration
     */
    public static final Duration MIN_TIMEOUT = Duration.ZERO;
    
    /**
     * The maximum timeout duration
     */
    public static final Duration MAX_TIMEOUT = Duration.ofMillis(Integer.MAX_VALUE);
}
