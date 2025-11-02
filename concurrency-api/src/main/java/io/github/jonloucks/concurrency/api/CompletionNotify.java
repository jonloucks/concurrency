package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

/**
 * Responsibility: Dispatch Completion status to subscribers
 */
public interface CompletionNotify<T> {

    AutoClose notify(OnCompletion<T> onCompletion);
}
