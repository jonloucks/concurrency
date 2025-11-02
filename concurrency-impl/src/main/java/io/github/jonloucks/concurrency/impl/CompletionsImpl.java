package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.impl.Internal.onCompletionCheck;
import static io.github.jonloucks.contracts.api.Checks.*;

final class CompletionsImpl implements Completions {

    @Override
    public <T> Completable<T> createCompletable(Completable.Config<T> config) {
        return new CompletableImpl<>(this.config, configCheck(config));
    }
   
    @Override
    public <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer) {
        final Consumer<Completable.Config.Builder<T>> validBuilderConsumer = builderConsumerCheck(builderConsumer);
        final Completable.Config.Builder<T> builder = new Completable.Config.Builder<>() {};
        validBuilderConsumer.accept(builder);
        return createCompletable(builder);
    }
    
    @Override
    public <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer) {
        final Consumer<Completion.Config.Builder<T>> validConsumerBuilder = builderConsumerCheck(builderConsumer);
        final CompletionBuilderImpl<T> builder = new CompletionBuilderImpl<>();
        validConsumerBuilder.accept(builder);
        return createCompletion(builder);
    }
    
    @Override
    public <T> Completion<T> createCompletion(Completion.Config<T> config) {
        return new ImmutableCompletionImpl<>(config);
    }
    
    @Override
    public <T> void completeLater(OnCompletion<T> onCompletion, Consumer<OnCompletion<T>> delegate) {
        final OnCompletion<T> validOnCompletion = onCompletionCheck(onCompletion);
        final Completion.Config.Builder<T> builder = new CompletionBuilderImpl<>();
        boolean delegated = false;
        try {
            delegateCheck(delegate).accept(validOnCompletion); // note: illegal check inside try
            delegated = true;
        } catch (Throwable thrown) {
            builder.state(Completion.State.FAILED).thrown(thrown);
            throw thrown;
        } finally {
            if (!delegated) {
                validOnCompletion.onCompletion(builder);
            }
        }
    }
    
    @Override
    public <T> T completeNow(OnCompletion<T> onCompletion, Supplier<T> successBlock) {
        final OnCompletion<T> validOnCompletion = onCompletionCheck(onCompletion);
        final Completion.Config.Builder<T> builder = new CompletionBuilderImpl<>();
        try {
            final T value = successBlockCheck(successBlock).get(); // note: illegal check inside try
            builder.value(value).state(Completion.State.SUCCEEDED);
            return value;
        } catch (Throwable thrown) {
            builder.state(Completion.State.FAILED).thrown(thrown);
            throw thrown;
        } finally {
            validOnCompletion.onCompletion(builder);
        }
    }
    
    CompletionsImpl(Concurrency.Config config) {
        this.config = config;
    }
    
    private static <T> Supplier<T> successBlockCheck(Supplier<T> block) {
        return nullCheck(block, "Success block must be Present.");
    }

    private static <T> Consumer<OnCompletion<T>> delegateCheck(Consumer<OnCompletion<T>> delegate) {
        return nullCheck(delegate, "Delegate must be present.");
    }
    
    private final Concurrency.Config config;
}
