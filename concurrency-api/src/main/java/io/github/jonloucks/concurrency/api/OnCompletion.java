package io.github.jonloucks.concurrency.api;

/**
 * Responsibility: to receive the information when an action or activity has finished.
 * Use for both asynchronous and synchronous actions. The only difference is when and on what
 * thread the callback is executed on
 * @param <T> the type of completion value
 */
@FunctionalInterface
public interface OnCompletion<T> {
    
    /**
     * Callback which receives the
     * @param completion the completion information.
     */
    void onCompletion(Completion<T> completion);
}
