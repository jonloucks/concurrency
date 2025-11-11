package io.github.jonloucks.concurrency;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.GlobalConcurrency;
import io.github.jonloucks.contracts.api.Checks;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;

/**
 * A placeholder class to make sure dependencies are correct for api and implementation.
 */
public final class Stub {
    
    /**
     * Utility class instantiation protection
     * Test coverage not possible, java module protections in place
     */
    private Stub() {
        // conflicting standards.  100% code coverage vs throwing exception on instantiation of utility class.
        // Java modules protects agents invoking private methods.
        // There are unit tests that will fail if this constructor is not private
    }
    
    /**
     * Validates basic functionality.
     */
    public static void validate() {
        //noinspection ResultOfMethodCallIgnored
        GlobalConcurrency.getInstance();
        validate(GlobalContracts.getInstance());
    }
    
    public static void validate(Contracts contracts) {
        Checks.validateContracts(contracts);
        final Concurrency concurrency = contracts.claim(Concurrency.CONTRACT);
    }
}
