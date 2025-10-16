package io.github.jonloucks.concurrency.impl;

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

    interface ThrowingRunnable {
        void run() throws Throwable;
    }
    static void runWithIgnore(ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignore) {
        
        }
    }
}
