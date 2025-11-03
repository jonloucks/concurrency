package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.OnCompletion;

import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.impl.Internal.onCompletionCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class CompleteNowImpl<T> {
    
    CompleteNowImpl(OnCompletion<T> onCompletion, Supplier<T> successBlock) {
        this.onCompletion = onCompletionCheck(onCompletion);
        this.successBlock = successBlock;
    }
    
    T run() {
        final Completion.Config.Builder<T> builder = new CompletionBuilderImpl<>();
        try {
            final T value = successBlockCheck(successBlock).get(); // note: illegal check inside try
            builder.value(value).state(Completion.State.SUCCEEDED);
            return value;
        } catch (Throwable thrown) {
            builder.state(Completion.State.FAILED).thrown(thrown);
            throw thrown;
        } finally {
            onCompletion.onCompletion(builder);
        }
    }
    
    private static <T> Supplier<T> successBlockCheck(Supplier<T> block) {
        return nullCheck(block, "Success block must be Present.");
    }
    
    private final OnCompletion<T> onCompletion;
    private final Supplier<T> successBlock;
}
