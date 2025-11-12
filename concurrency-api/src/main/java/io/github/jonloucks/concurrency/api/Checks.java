package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.ContractException;
import io.github.jonloucks.contracts.api.Contracts;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static io.github.jonloucks.contracts.api.Checks.validateContracts;

/**
 * Checks used internally and supported for external use.
 */
public final class Checks {
    
    /**
     * Check if given Concurrency is not null or invalid
     *
     * @param concurrency the Concurrency to check
     * @return a valid Concurrency
     * @throws IllegalArgumentException when invalid
     */
    public static Concurrency concurrencyCheck(Concurrency concurrency) {
        return nullCheck(concurrency, "Concurrency must be present.");
    }

    /**
     * Check if given OnCompletion is not null or invalid
     *
     * @param onCompletion the OnCompletion to check
     * @return a valid OnCompletion
     * @param <T> the completion type
     * @throws IllegalArgumentException when invalid
     */
    public static <T> OnCompletion<T> onCompletionCheck(OnCompletion<T>  onCompletion) {
        return nullCheck(onCompletion, "OnCompletion must be present.");
    }
    
    /**
     * Check if given StateMachine is not null or invalid
     *
     * @param stateMachine the StateMachine to check
     * @return a valid StateMachine
     * @param <T> the state type
     * @throws IllegalArgumentException when invalid
     */
    public static <T> StateMachine<T>  stateMachineCheck(StateMachine<T> stateMachine) {
        return nullCheck(stateMachine, "StateMachine must be present.");
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
    public static void validateConcurrency(Contracts contracts, Concurrency concurrency) {
        final Concurrency validConcurrency = concurrencyCheck(concurrency);
        validateContracts(contracts);
        
        final Concurrency promisedConcurrency = contracts.claim(Concurrency.CONTRACT);
        if (validConcurrency != promisedConcurrency) {
            throw new ConcurrencyException("Concurrency.CONTRACT claim is different.");
        }
        contracts.claim(CompletableFactory.CONTRACT);
        contracts.claim(CompletionFactory.CONTRACT);
        contracts.claim(ConcurrencyFactory.CONTRACT);
        contracts.claim(StateMachineFactory.CONTRACT);
        contracts.claim(WaitableFactory.CONTRACT);
    }
    
    private Checks() {
    
    }
}
