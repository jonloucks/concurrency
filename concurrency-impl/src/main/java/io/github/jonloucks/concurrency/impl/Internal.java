package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.concurrency.api.StateMachine;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.api.Constants.MAX_TIMEOUT;
import static io.github.jonloucks.contracts.api.Checks.illegalCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class Internal {
    
    /**
     * Utility class instantiation protection
     * Test coverage not possible, java module protections in place
     */
    private Internal() {
        // conflicting standards.  100% code coverage vs throwing exception on instantiation of utility class.
        // Java modules protects agents invoking private methods.
        // There are unit tests that will fail if this constructor is not private
    }
    
    static <T> T stateCheck(T state) {
        return nullCheck(state, "Rule must be present.");
    }
    
    static String eventCheck(String event) {
        return nullCheck(event, "Event must be present.");
    }
    
    static <T> StateMachine.Rule<T> ruleCheck(StateMachine.Rule<T> rule) {
        return nullCheck(rule, "Rule must be present.");
    }
    
    static <T> Predicate<T> predicateCheck(Predicate<T> predicate) {
        return nullCheck(predicate, "Predicate must be present.");
    }
    
    static <T> Consumer<T> listenerCheck(Consumer<T> consumer) {
        return nullCheck(consumer, "Listener must be present.");
    }
    
    static Duration timeoutCheck(Duration timeout) {
        final Duration notNullTimeout = nullCheck(timeout, "Timeout must be present.");
        illegalCheck(timeout, timeout.isNegative(), "Timeout must not be negative.");
        illegalCheck(timeout, timeout.compareTo(MAX_TIMEOUT) > 0, "Timeout must less than or equal to maximum time.");
        return notNullTimeout;
    }
    
    static <T> Completion<T> completionCheck(Completion<T> completion) {
        return nullCheck(completion, "Completion must be present.");
    }
    
    static <T> OnCompletion<T> onCompletionCheck(OnCompletion<T> onCompletion) {
        return nullCheck(onCompletion, "OnCompletion must be present.");
    }
    
    static long getWaitMillis(Duration timeout, Instant start, Instant end) {
        return Long.max(1, timeout.minus(Duration.between(start, end)).toMillis());
    }
    
    static boolean hasTimedOut(Duration timeout, Instant start, Instant end) {
        return Duration.between(start, end).compareTo(timeout) >= 0;
    }

    static void validate() {
        runWithIgnore(() -> { throw new IOException("Validate"); });
        runWithIgnore(() -> { throw new InterruptedException("Validate"); });
    }
    
    @FunctionalInterface
    interface ThrowingRunnable<E extends Throwable> {
        void run() throws E;
    }
    
    static <E extends Throwable> void runWithIgnore(ThrowingRunnable<E> runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }
}
