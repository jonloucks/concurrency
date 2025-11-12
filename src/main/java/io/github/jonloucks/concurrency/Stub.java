package io.github.jonloucks.concurrency;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.concurrency.api.GlobalConcurrency;
import io.github.jonloucks.contracts.api.ContractException;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;

import static io.github.jonloucks.concurrency.api.Checks.validateConcurrency;

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
     * Quickly validates Global Contracts and Concurrency
     *
     * @throws ContractException when invalid
     * @throws ConcurrencyException when invalid
     * @throws IllegalArgumentException when invalid
     */
    public static void validate() {
        validate(GlobalContracts.getInstance(), GlobalConcurrency.getInstance());
    }
    
    /**
     * Quickly validates a Contracts and Concurrency
     *
     * @param contracts the Contracts to validate
     * @param concurrency the Concurrency to validate
     *
     * @throws ContractException when invalid
     * @throws ConcurrencyException when invalid
     * @throws IllegalArgumentException when invalid
     */
    public static void validate(Contracts contracts, Concurrency concurrency) {
        validateConcurrency(contracts, concurrency);
    }
}
