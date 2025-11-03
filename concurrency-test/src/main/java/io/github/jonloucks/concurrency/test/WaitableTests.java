package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Constants.MIN_TIMEOUT;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.concurrency.test.WaitableTests.WaitableTestsTools.INITIAL;
import static io.github.jonloucks.concurrency.test.WaitableTests.WaitableTestsTools.MODIFIED;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface WaitableTests {
    
    @Test
    default void waitable_CreateWithValidInitial_Works() {
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
    default void waitable_acceptWhen_WithNullPredicate_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Supplier<String> valueSupplier = () -> INITIAL;
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptWhen(null, valueSupplier);
            });
            
            assertThrown(thrown, "Predicate must be present.");
        });
    }
    
    @Test
    default void waitable_acceptIf_WithNullValueSupplier_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = s -> true;
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptIf(predicate, (Supplier<String>)null);
            });
            
            assertThrown(thrown, "Value supplier must be present.");
        });
    }
    
    @Test
    default void waitable_acceptIf_WithNullPredicateAndValue_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptIf(null, MODIFIED);
            });
            
            assertThrown(thrown, "Predicate must be present.");
        });
    }

    @Test
    default void waitable_acceptIf_WithNullPredicateAndSupplier_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Supplier<String> initialSupplier = () -> INITIAL;
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.acceptIf(null, initialSupplier);
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
    default void waitable_getWhen_WithPredicateAndTooLongOfTimeout_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = s -> true;
            final Duration timeout = Duration.ofSeconds(Long.MAX_VALUE);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                waitable.getWhen(predicate, timeout);
            });
            
            assertThrown(thrown, "Timeout must less than or equal to maximum time.");
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
    default void waitable_acceptWhen_WhenSuccess_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> accepted = waitable.acceptWhen(INITIAL::equals, () -> MODIFIED);
            
            assertNotNull(accepted);
            assertTrue(accepted.isPresent());
        });
    }
    
    @Test
    default void waitable_acceptWhen_WhenFailedAndZeroTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> accepted = waitable.acceptWhen("abc"::equals, () -> MODIFIED, MIN_TIMEOUT);
            
            assertNotNull(accepted);
            assertFalse(accepted.isPresent());
        });
    }
    
    @Test
    default void waitable_acceptWhen_WhenPassingAndZeroTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> accepted = waitable.acceptWhen(INITIAL::equals, () -> MODIFIED, MIN_TIMEOUT);
            
            assertNotNull(accepted);
            assertTrue(accepted.isPresent());
            assertEquals(INITIAL, accepted.get());
        });
    }
    
    @Test
    default void waitable_acceptWhen_WhenPassingAndBeforeTimeout_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Thread thread = new Thread(() -> {
                for (int i = 1; i <= 1_000; i++) {
                    waitable.accept(Integer.toString(i));
                }
                waitable.accept(MODIFIED);
            });
            thread.setDaemon(true);
            thread.start();
            
            final Optional<String> accepted = waitable.acceptWhen(MODIFIED::equals, () -> "Hello", Duration.ofSeconds(1));
            
            assertNotNull(accepted);
            assertTrue(accepted.isPresent());
            assertEquals(MODIFIED, accepted.get());
            assertEquals("Hello", waitable.get());
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
            
            final Runnable runnable = () -> {
                final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMinutes(5));
                if (!optionalValue.isPresent()) {
                    throw new AssertionError("Failed getWhen, value must be present.");
                }
            };
            
            final SpawnThreads spawnThreads = new SpawnThreads(numberOfThreads, runnable);
            spawnThreads.start();
            waitable.accept(MODIFIED);
            spawnThreads.finish();
        });
    }
    
    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1,3,17})
    default void waitable_getWhen_shutdownWithThreads_Works(int numberOfThreads) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Runnable runnable = () -> {
                final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMinutes(5));
                if (optionalValue.isPresent()) {
                    throw new AssertionError("Failed getWhen, not expecting present.");
                }
            };
            
            final SpawnThreads spawnThreads = new SpawnThreads(numberOfThreads, runnable);
            spawnThreads.start();
            waitable.shutdown();
            spawnThreads.finish();
        });
    }
    
    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1,3,17})
    default void waitable_getWhen_WithThreadsAndTimeout_Works(int numberOfThreads) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Runnable runnable = () -> {
                final Optional<String> optionalValue = waitable.getWhen(MODIFIED::equals, Duration.ofMillis(25));
                if (optionalValue.isPresent()) {
                    throw new AssertionError("Failed getWhen, not expecting present.");
                }
            };
            
            final SpawnThreads spawnThreads = new SpawnThreads(numberOfThreads, runnable);
            spawnThreads.start();
            spawnThreads.finish();
        });
    }

    @Test
    default void waitable_accept_WithPredicateAndValue_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
      
            waitable.accept(MODIFIED);
            
            assertEquals(MODIFIED, waitable.get());
        });
    }
    
    @Test
    default void waitable_acceptIf_WithValue_Passing_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            Optional<String> optionalOldValue = waitable.acceptIf( INITIAL::equals, MODIFIED);
            
            assertTrue(optionalOldValue.isPresent());
            assertEquals(INITIAL, optionalOldValue.get());
            assertEquals(MODIFIED, waitable.get());
        });
    }
    
    @Test
    default void waitable_acceptIf_WithValueSupplier_Passing_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            Optional<String> optionalOldValue = waitable.acceptIf( INITIAL::equals, ()-> MODIFIED);
            
            assertTrue(optionalOldValue.isPresent());
            assertEquals(INITIAL, optionalOldValue.get());
            assertEquals(MODIFIED, waitable.get());
        });
    }
    
    @Test
    default void waitable_acceptIf_WithValue_Failed_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> oldValue = waitable.acceptIf( "unknown"::equals, MODIFIED);
            
            assertFalse(oldValue.isPresent());
            assertEquals(INITIAL, waitable.get());
        });
    }
    
    @Test
    default void waitable_acceptIf_WithValueSupplier_Failed_Works() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            final Optional<String> oldValue = waitable.acceptIf( "unknown"::equals, () -> MODIFIED);
            
            assertFalse(oldValue.isPresent());
            assertEquals(INITIAL, waitable.get());
        });
    }
    
    @Test
    default void waitable_notifyIf_WithNullPredicate_Throws() {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Consumer<String> listener = t -> {};
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                //noinspection resource
                waitable.notifyIf(null, listener);
            });
            assertThrown(thrown, "Predicate must be present.");
        });
    }
    
    @Test
    default void waitable_notifyIf_WithValid_Works(@Mock Consumer<String> listener) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> !INITIAL.equals(v), listener)) {
                ignore(closeNotify);
                waitable.accept(MODIFIED);
                verify(listener, times(1)).accept(eq(MODIFIED));
            }
            
            waitable.accept("listener should not receive this value");
            
            verify(listener, times(1)).accept(any());
        });
    }
    
    @Test
    default void waitable_notifyIf_WithSameListener_Works(@Mock Consumer<String> listener) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> !INITIAL.equals(v), listener);
                 AutoClose closeNotify2 = waitable.notifyIf(v -> !INITIAL.equals(v), listener)) {
                ignore(closeNotify); ignore(closeNotify2);
                waitable.accept(MODIFIED);
                verify(listener, times(2)).accept(eq(MODIFIED));
            }
            
            waitable.accept("listener should not receive this value");
            
            verify(listener, times(2)).accept(any());
        });
    }
    
    @Test
    default void waitable_notifyIf_SameValue_NotifiesOnce(@Mock Consumer<String> listener) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> !INITIAL.equals(v), listener)) {
                ignore(closeNotify);
                waitable.accept(MODIFIED);
                waitable.accept(MODIFIED);
                
                verify(listener, times(1)).accept(eq(MODIFIED));
            }
        });
    }
    
    @Test
    default void waitable_notifyIf_ManyValueChanges_Works(@Mock Consumer<Integer> listener) {
        withConcurrency((contracts,concurrency)-> {
            final Integer initial = 1;
            final Waitable<Integer> waitable = concurrency.createWaitable(0);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> !initial.equals(v), listener)) {
                final int changeCount = 1_000;
                ignore(closeNotify);
                for (int i = 1; i <= changeCount; i++) {
                    waitable.accept(i);
                }
                verify(listener, times(changeCount)).accept(any());
            }
        });
    }
    
    @Test
    default void waitable_notifyIf_WithManyConcurrentChanges_Works(@Mock Consumer<Integer> listener) {
        withConcurrency((contracts,concurrency) -> {
            final Integer initial = 0;
            final Waitable<Integer> waitable = concurrency.createWaitable(initial);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> v % 2 == 0, listener)) {
                ignore(closeNotify);
                final Thread thread = new Thread(() -> {
                    for (int n = 1; n <= 100_000; n++) {
                        waitable.accept(n);
                    }
                });
                thread.setDaemon(true);
                thread.start();
                sleep(Duration.ofMillis(10));
            }
            
            verify(listener, atLeast(1)).accept(any());
        });
    }
    
    @Test
    default void waitable_notifyIf_Idempotent_Works(@Mock Consumer<String> listener) {
        withConcurrency((contracts,concurrency)-> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            
            try (AutoClose closeNotify = waitable.notifyIf(v -> !INITIAL.equals(v), listener)) {
                assertDoesNotThrow(closeNotify::close);
                assertDoesNotThrow(closeNotify::close);
            }
            
            waitable.accept("listener should not receive this value");
            
            verify(listener, times(0)).accept(any());
        });
    }
    
    @Test
    default void waitable_notifyIf_WithNullListener_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final Waitable<String> waitable = concurrency.createWaitable(INITIAL);
            final Predicate<String> predicate = t -> true;
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                //noinspection resource
                waitable.notifyIf(predicate, null);
            });
            assertThrown(thrown, "Listener must be present.");
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
