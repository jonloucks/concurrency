package io.github.jonloucks.concurrency.api;

import static io.github.jonloucks.contracts.api.Checks.messageCheck;

/**
 * Runtime exception thrown for Concurrency related problems.
 * For example, when Concurrency fails to initialize.
 */
public class ConcurrencyException extends RuntimeException {
    
    private static final long serialVersionUID = 7311228400588901174L;
    
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
}
