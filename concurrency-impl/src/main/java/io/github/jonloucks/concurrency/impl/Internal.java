package io.github.jonloucks.concurrency.impl;

import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Promisor;
import io.github.jonloucks.contracts.api.Promisors;

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
    
    static <T> Predicate<T> predicateCheck(Predicate<T> predicate) {
        return nullCheck(predicate, "Predicate must be present.");
    }
    
    static Duration timeoutCheck(Duration timeout) {
        final Duration validTimeout = nullCheck(timeout, "Timeout must be present.");
        return illegalCheck(validTimeout, validTimeout.isNegative(), "Timeout must not be negative.");
    }
    
    static <T> Promisor<T> lifeCycle(Contracts contracts, Promisor<T> promisor) {
        final Promisors promisors = contracts.claim(Promisors.CONTRACT);
        return promisors.createLifeCyclePromisor(promisor);
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
