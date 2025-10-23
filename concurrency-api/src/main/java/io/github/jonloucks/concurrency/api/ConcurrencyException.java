package io.github.jonloucks.concurrency.api;

import static io.github.jonloucks.contracts.api.Checks.messageCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * Runtime exception thrown for Concurrency related problems.
 * For example, when Concurrency fails to initialize.
 */
public class ConcurrencyException extends RuntimeException {

    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String)}
     *
     * @param message the message for this exception
     */
    public ConcurrencyException(String message) {
        this(message, null);
    }
    
    /**
     * Passthrough for {@link RuntimeException#RuntimeException(String, Throwable)}
     *
     * @param message the message for this exception
     * @param thrown  the cause of this exception, null is allowed
     */
    public ConcurrencyException(String message, Throwable thrown) {
        super(messageCheck(message), thrown);
    }
    
    /**
     * Rethrows a caught exception or a ConcurrencyException if unchecked
     *
     * @param thrown the previously caught exception
     * @return Possibly a new ConcurrencyException which should be rethrown
     * @throws Error iif original thrown is an error
     * @throws RuntimeException when thrown is a RuntimeException
     * @throws ConcurrencyException if thrown is a checked exception
     */
    @SuppressWarnings("UnusedReturnValue")
    public static ConcurrencyException rethrow(Throwable thrown) throws Error, RuntimeException {
        final Throwable validThrown = nullCheck(thrown, "Thrown must be present.");

        if (validThrown instanceof Error) {
            throw (Error) validThrown;
        } else if (validThrown instanceof RuntimeException) {
            throw (RuntimeException) validThrown;
        }
        
        return new ConcurrencyException(validThrown.getMessage(), validThrown);
    }
    
    /**
     * Imposed serialization from RuntimeException
     */
    @SuppressWarnings("serial")
    private static final long serialVersionUID = 1L;
}
