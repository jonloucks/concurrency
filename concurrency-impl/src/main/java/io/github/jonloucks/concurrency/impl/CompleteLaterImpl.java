package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.OnCompletion;

import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.impl.Internal.onCompletionCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class CompleteLaterImpl<T> {
    CompleteLaterImpl(OnCompletion<T> onCompletion, Consumer<OnCompletion<T>> delegate) {
        this.onCompletion = onCompletionCheck(onCompletion);
        this.delegate = delegate;
    }
    
    void run() {
        final Completion.Config.Builder<T> builder = new CompletionBuilderImpl<>();
        boolean delegated = false;
        try {
            delegateCheck(delegate).accept(onCompletion); // note: illegal check inside try
            delegated = true;
        } catch (Throwable thrown) {
            builder.state(Completion.State.FAILED).thrown(thrown);
            throw thrown;
        } finally {
            if (!delegated) {
                onCompletion.onCompletion(builder);
            }
        }
    }
    
    private static <T> Consumer<OnCompletion<T>> delegateCheck(Consumer<OnCompletion<T>> delegate) {
        return nullCheck(delegate, "Delegate must be present.");
    }
    
    private final OnCompletion<T> onCompletion;
    private final Consumer<OnCompletion<T>> delegate;
}
