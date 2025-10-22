package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Waitable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.concurrency.test.WaitableTests.WaitableTestsTools.INITIAL;
import static io.github.jonloucks.concurrency.test.WaitableTests.WaitableTestsTools.MODIFIED;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
public interface WaitableTests {
    
    @Test
    default void waitable_WithNullInitialValue_Throws() {
        withConcurrency((contracts,concurrency)-> {
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createWaitable(null);
            });
 
            assertThrown(thrown, "Value must be present.");
        });
    }
    
    @Test
    default void waitable_WithValidInitial_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            assertObject(waitable);
            assertEquals(INITIAL, waitable.get());
            assertTrue(waitable.getIf(INITIAL::equals).isPresent());
            assertEquals(INITIAL, waitable.getIf(INITIAL::equals).get());
            assertFalse(waitable.getIf(MODIFIED::equals).isPresent());
        });
    }
    
    @Test
    default void waitable_accept_WithNullValue_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.accept(null);
            });
            
            assertThrown(thrown, "Value must be present.");
        });
    }
    
    @Test
    default void waitable_acceptIf_WithNullValue_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = s -> true;
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptIf(predicate, null);
            });
            
            assertThrown(thrown, "Value must be present.");
        });
    }
    
    @Test
    default void waitable_acceptIf_WithNullPredicate_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptIf(null, MODIFIED);
            });
            
            assertThrown(thrown, "Predicate must be present.");
        });
    }
    
    @Test
    default void waitable_getWhen_WithNullPredicate_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.getWhen(null);
            });
            
            assertThrown(thrown, "Predicate must be present.");
        });
    }
    
    @Test
    default void waitable_getWhen_WithNullPredicateAndTimeout_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Duration timeout = Duration.ofSeconds(1);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.getWhen(null, timeout);
            });
            
            assertThrown(thrown, "Predicate must be present.");
        });
    }
    
    @Test
    default void waitable_getWhen_WithPredicateAndNullTimeout_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = s -> true;
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.getWhen(predicate, null);
            });
            
            assertThrown(thrown, "Timeout must be present.");
        });
    }
    
    @Test
    default void waitable_getWhen_WithPredicateAndNegativeTimeout_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = s -> true;
            final Duration timeout = Duration.ofSeconds(-1);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.getWhen(predicate, timeout);
            });
            
            assertThrown(thrown, "Timeout must not be negative.");
        });
    }
    
    @Test
    default void waitable_getWhen_WithInitialValue_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> optionalValue = waitable.getWhen(INITIAL::equals);
            
            assertTrue(optionalValue.isPresent());
            assertEquals(INITIAL, optionalValue.get());
        });
    }
    
    @Test
    @Deprecated
    default void waitable_waitFor_WithInitialValue_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> optionalValue = waitable.waitFor(INITIAL::equals);
            
            assertTrue(optionalValue.isPresent());
            assertEquals(INITIAL, optionalValue.get());
        });
    }
    
    @Test
    default void waitable_accept_ValueNotChanged_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            waitable.accept(INITIAL);
            
            assertEquals(INITIAL, waitable.get());
        });
    }
    
    @Test
    default void waitable_getWhen_WithInitialValueAndTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> optionalValue = waitable.getWhen(INITIAL::equals, Duration.ofDays(1));
            
            assertTrue(optionalValue.isPresent());
            assertEquals(INITIAL, optionalValue.get());
        });
    }
    
    @Test
    @Deprecated
    default void waitable_waitFor_WithInitialValueAndTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> optionalValue = waitable.waitFor(INITIAL::equals, Duration.ofDays(1));
            
            assertTrue(optionalValue.isPresent());
            assertEquals(INITIAL, optionalValue.get());
        });
    }
    
    @Test
    default void waitable_getWhen_WithFailedAndTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Thread thread = new Thread(() -> {
                for (int n = 0; n < 1_000; n++) {
                    waitable.accept(UUID.randomUUID().toString());
                }
            });
            thread.start();
            
            final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMillis(100));
            assertFalse(optionalValue.isPresent());
        });
    }
    
    @Test
    default void waitable_getWhen_WithFailedAndZeroTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ZERO);
            assertFalse(optionalValue.isPresent());
            assertEquals(INITIAL, waitable.get());
        });
    }
    
    @Test
    default void waitable_getWhen_WithFailedWhenShutdown_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            waitable.shutdown();
            final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMinutes(5));
            assertFalse(optionalValue.isPresent());
            assertEquals(INITIAL, waitable.get());
        });
    }
    
    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1,3,17})
    default void waitable_getWhen_Threads_Works(int numberOfThreads) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
            final AtomicInteger errorCount = new AtomicInteger(0);
            
            for (int i = 0; i < numberOfThreads; i++) {
                final Thread thread = new Thread(() -> {
                    try {
                        final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMinutes(5));
                        if (optionalValue.isPresent()) {
                            countDownLatch.countDown();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    } catch (Throwable ignored) {
                        errorCount.incrementAndGet();
                    }
                });
                thread.start();
            }
            
            waitable.accept(MODIFIED);
            try {
                final boolean finished = countDownLatch.await(5, TimeUnit.MINUTES);
                assertTrue(finished, "Timed out waiting for threads to finish.");
                assertEquals(0, errorCount.get(), "Errors were found.");
            } catch (InterruptedException ignored) {
                throw new AssertionError("Interrupted waiting for threads to finish.");
            }
        });
    }

    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1,3,17})
    default void waitable_getWhen_shutdownWithThreads_Works(int numberOfThreads) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
            final AtomicInteger errorCount = new AtomicInteger(0);
            
            for (int i = 0; i < numberOfThreads; i++) {
                final Thread thread = new Thread(() -> {
                    try {
                        final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMinutes(5));
                        if (!optionalValue.isPresent()) {
                            countDownLatch.countDown();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    } catch (Throwable ignored) {
                        errorCount.incrementAndGet();
                    }
                });
                thread.start();
            }
            waitable.shutdown();
            try {
                final boolean finished = countDownLatch.await(5, TimeUnit.MINUTES);
                assertTrue(finished, "Timed out waiting for threads to finish.");
                assertEquals(0, errorCount.get(), "Errors were found.");
            } catch (InterruptedException ignored) {
                throw new AssertionError("Interrupted waiting for threads to finish.");
            }
        });
    }
    
    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1,3,17})
    default void waitable_getWhen_WithThreadsAndTimeout_Works(int numberOfThreads) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
            final AtomicInteger errorCount = new AtomicInteger(0);
            
            for (int i = 0; i < numberOfThreads; i++) {
                final Thread thread = new Thread(() -> {
                    try {
                        final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMillis(25));
                        if (!optionalValue.isPresent()) {
                            countDownLatch.countDown();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    } catch (Throwable ignored) {
                        errorCount.incrementAndGet();
                    }
                });
                thread.start();
            }
            try {
                final boolean finished = countDownLatch.await(5, TimeUnit.MINUTES);
                assertTrue(finished, "Timed out waiting for threads to finish.");
                assertEquals(0, errorCount.get(), "Errors were found.");
            } catch (InterruptedException ignored) {
                throw new AssertionError("Interrupted waiting for threads to finish.");
            }
        });
    }
    
    @Test
    default void waitable_accept_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
      
            waitable.accept(MODIFIED);
            assertEquals(MODIFIED, waitable.get());
            assertTrue(waitable.getIf(MODIFIED::equals).isPresent());
            assertEquals(MODIFIED, waitable.getIf(MODIFIED::equals).get());
            assertFalse(waitable.getIf(INITIAL::equals).isPresent());
        });
    }
    
    @Test
    default void waitable_acceptIf_Passing_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            Optional<String> optionalOldValue = waitable.acceptIf( INITIAL::equals, MODIFIED);
            
            assertTrue(optionalOldValue.isPresent());
            assertEquals(INITIAL, optionalOldValue.get());
            assertEquals(MODIFIED, waitable.get());
            assertTrue(waitable.getIf(MODIFIED::equals).isPresent());
            assertEquals(MODIFIED, waitable.getIf(MODIFIED::equals).get());
            assertFalse(waitable.getIf(INITIAL::equals).isPresent());
        });
    }
    
    @Test
    default void waitable_acceptIf_Failed_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> oldValue = waitable.acceptIf( "unknown"::equals, MODIFIED);
            
            assertFalse(oldValue.isPresent());
            assertEquals(INITIAL, waitable.get());
            assertTrue(waitable.getIf(INITIAL::equals).isPresent());
            assertEquals(INITIAL, waitable.getIf(INITIAL::equals).get());
            assertFalse(waitable.getIf(MODIFIED::equals).isPresent());
        });
    }
    
    @Test
    default void waitable_InternalCoverage() {
        assertInstantiateThrows(WaitableTestsTools.class);
    }
    
    final class WaitableTestsTools {
        public static final String INITIAL = "initial";
        public static final String MODIFIED = "modified";
        
        private WaitableTestsTools() {
            throw new AssertionError("Illegal constructor.");
        }
    }
}
