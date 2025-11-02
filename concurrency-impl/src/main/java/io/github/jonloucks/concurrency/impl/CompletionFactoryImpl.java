package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.CompletionFactory;

import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.Checks.builderConsumerCheck;

final class CompletionFactoryImpl implements CompletionFactory {
    
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
    
    CompletionFactoryImpl() {
    
    }
}
