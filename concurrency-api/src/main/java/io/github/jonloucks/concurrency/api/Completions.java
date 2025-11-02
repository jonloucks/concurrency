package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.Contract;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *  Responsibility: An umbrella API for frequently used Completion features.
 *  <p>
 *  A Guaranteed Execution design.
 *  </p>
 *  Operations with API's that take OnCompletion must ensure it is completed at some point.
 *  An operation completes with success flag, optional value, optional exception
 */
public interface Completions {
    
    /**
     * The Contract for the Completions interface
     */
    Contract<Completions> CONTRACT = Contract.create(Completions.class);

    <T> Completable<T> createCompletable(Completable.Config<T> config);
    
    <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer);
    
    <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer);
    
    <T> Completion<T> createCompletion(Completion.Config<T> config);
    
    /**
     * Guaranteed execution: complete later block.
     * Either the delegate successfully takes ownership of the OnCompletion or
     * a final {@link io.github.jonloucks.concurrency.api.Completion.State#FAILED} completion is dispatched
     *
     * @param onCompletion the OnCompletion callback
     * @param delegate the intended delegate to receive the OnCompletion
     * @param <T> the completion value type
     */
    <T> void completeLater(OnCompletion<T> onCompletion, Consumer<OnCompletion<T>> delegate);
    
    /**
     * Guaranteed execution: complete now block
     * When this method finishes, it is guaranteed the OnCompletion will have received a final completion.
     * Exceptions will result in a {@link io.github.jonloucks.concurrency.api.Completion.State#FAILED} completion
     * Exceptions will be rethrown.
     *
     * @param onCompletion the OnCompletion callback
     * @param successBlock executed to determine the final completion value for an activity
     * @return the final completion value
     * @param <T> the completion value type
     */
    <T> T completeNow(OnCompletion<T> onCompletion, Supplier<T> successBlock);
}
