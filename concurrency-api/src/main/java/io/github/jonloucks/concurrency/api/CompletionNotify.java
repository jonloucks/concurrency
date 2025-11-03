package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

/**
 * Responsibility: Dispatch Completion status to subscribers
 */
public interface CompletionNotify<T> {
    
    /**
     * Open a notification subscription for receive completions
     *
     * @param onCompletion the completion
     * @return the
     */
    AutoClose notify(OnCompletion<T> onCompletion);
}
