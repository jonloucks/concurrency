package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.GlobalContracts;

import static io.github.jonloucks.contracts.api.Checks.validateContracts;

/**
 * Provides runtime validation
 */
public final class Stub {
    
    private Stub() {
    }
    
    /**
     * Provides runtime validation
     */
    public static void validate() {
        validateContracts(GlobalContracts.getInstance());
        Internal.validate();
    }
}
