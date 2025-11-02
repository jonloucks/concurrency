package io.github.jonloucks.concurrency.api;

/**
 * Responsibility:
 * @param <T>
 */
@FunctionalInterface
public interface OnCompletion<T> {
    void onCompletion(Completion<T> completion);
}
