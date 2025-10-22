package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.concurrency.api.StateMachine;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Predicate;

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
    
    static Duration timeoutCheck(Duration timeout) {
        final Duration validTimeout = nullCheck(timeout, "Timeout must be present.");
        return illegalCheck(validTimeout, validTimeout.isNegative(), "Timeout must not be negative.");
    }
    
    static void throwUnchecked(Throwable thrown, String message) throws Error,  RuntimeException {
        if (null == thrown) {
            return;
        } else if (thrown instanceof Error) {
            throw (Error) thrown;
        } else if (thrown instanceof RuntimeException) {
            throw (RuntimeException) thrown;
        }
        throw new ConcurrencyException(message, thrown);
    }
    
    static void validate() {
        validateUnchecked(new Error(), "Some error.");
        validateUnchecked(new IOException(), "Some IO error.");
        validateUnchecked(new ConcurrencyException("Hello."), "Some concurrency error.");
        validateUnchecked(null, "Some concurrency error.");
    }
    
    private static void validateUnchecked(Throwable thrown, String message) throws Error, ConcurrencyException {
        runWithIgnore( () -> throwUnchecked(thrown, message));
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
