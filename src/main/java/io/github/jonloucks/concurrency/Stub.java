package io.github.jonloucks.concurrency;

import io.github.jonloucks.concurrency.api.*;
import io.github.jonloucks.contracts.api.Checks;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;

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
        validate(GlobalContracts.getInstance(), GlobalConcurrency.getInstance());
    }
    
    /**
     * Validates basic functionality for a given Contracts and Concurrency
     * @param contracts the Contracts to validate
     * @param concurrency the Concurrency to validate
     */
    public static void validate(Contracts contracts, Concurrency concurrency) {
        Checks.validateContracts(contracts);
        final Concurrency validConcurrency = nullCheck(concurrency, "Concurrency must be present.");
        contracts.claim(Concurrency.CONTRACT);
        contracts.claim(StateMachineFactory.CONTRACT);
        contracts.claim(CompletableFactory.CONTRACT);
        contracts.claim(WaitableFactory.CONTRACT);
        contracts.claim(CompletionFactory.CONTRACT);
        contracts.claim(ConcurrencyFactory.CONTRACT);
        final Waitable<String> waitable = validConcurrency.createWaitable("test");
        assert "test".equals(waitable.get());
    }
}
