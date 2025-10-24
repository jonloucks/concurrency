package io.github.jonloucks.concurrency.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SpawnThreads {
    SpawnThreads(int numberOfThreads, Runnable runnable) {
        this.countDownLatch = new CountDownLatch(numberOfThreads);
        this.threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    runnable.run();
                } catch (Throwable ignored) {
                    errorCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
            thread.setDaemon(true);
            threads[i] = thread;
        }
    }
    
    void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    void finish() {
        try {
            final boolean finished = countDownLatch.await(5, TimeUnit.MINUTES);
            assertTrue(finished, "Timed out waiting for threads to finish.");
            assertEquals(0, errorCount.get(), "Errors were found.");
        } catch (InterruptedException ignored) {
            throw new AssertionError("Interrupted waiting for threads to finish.");
        }
    }
    
    private final CountDownLatch countDownLatch;
    private final Thread[] threads;
    private final AtomicInteger errorCount = new AtomicInteger();
}
