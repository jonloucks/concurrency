package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Constants;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertObject;
import static org.junit.jupiter.api.Assertions.*;

public interface ConstantsTests {
    @Test
    default void constants_values() {
        assertObject(Constants.MAX_TIMEOUT);
        assertFalse(Constants.MAX_TIMEOUT.isNegative(), "Maximum timeout can not be negative");
        assertFalse(Constants.MAX_TIMEOUT.isZero(), "Maximum timeout can not be zero.");
        
        assertObject(Constants.MIN_TIMEOUT);
        assertFalse(Constants.MAX_TIMEOUT.isNegative(), "Minimum timeout can not be negative");
        
        assertTrue(Constants.MAX_TIMEOUT.compareTo(Constants.MIN_TIMEOUT) >= 0, "Maximum timeout must be greater than or equal to minimum timeout.");
    }
}
