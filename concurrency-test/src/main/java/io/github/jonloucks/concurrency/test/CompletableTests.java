package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Completable;
import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.contracts.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Completion.State.*;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface CompletableTests {
    
    @Test
    default void completable_create_withNullBuilderConsumer_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createCompletable((Consumer<Completable.Config.Builder<String>>) null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_create_withNullConfig_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                concurrency.createCompletable((Completable.Config<String>) null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_create_Defaults_Works() {
        withConcurrency((contracts, concurrency) -> {
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            
            assertObject(completable);
            assertFalse(completable.isCompleted());
            assertFalse(completable.getCompletion().isPresent());
            assertNotNull(completable.notifyState());
            assertNotNull(completable.notifyValue());
        });
    }
    
    @Test
    default void completable_create_WithConfig_Works() {
        withConcurrency((contracts, concurrency) -> {
            final Completable.Config<String> config = new Completable.Config<>() {
            };
            final Completable<String> completable = concurrency.createCompletable(config);
            
            assertObject(completable);
            assertFalse(completable.isCompleted());
            assertFalse(completable.getCompletion().isPresent());
            assertNotNull(completable.notifyState());
            assertNotNull(completable.notifyValue());
        });
    }
    
    @Test
    default void completable_onCompletion_WithoutOpen_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            final Completion<String> completion = concurrency.createCompletion(b -> b.state(FAILED));
            final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
                completable.onCompletion(completion);
            });
            assertThrown(thrown);
            assertObject(completable);
            assertFalse(completable.isCompleted());
            assertFalse(completable.getCompletion().isPresent());
        });
    }
    
    @Test
    default void completable_notify_WithNullOnCompletion_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose close = completable.open()) {
                ignore(close);
                final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                    //noinspection resource
                    completable.notify(null);
                });
                assertThrown(thrown);
            }
        });
    }
    
    @Test
    default void completable_notify_WithHappyPath_Works() {
        withConcurrency((contracts, concurrency) -> {
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose close = completable.open()) {
                ignore(close);
                final Completion<String> completion = concurrency.createCompletion(b -> b.state(FAILED));
                completable.onCompletion(completion);
                
                assertTrue(completable.isCompleted());
                assertTrue(completable.getCompletion().isPresent());
                assertEquals(completion, completable.getCompletion().get());
            }
        });
    }
    
    @Test
    default void completable_notify_WithHappyPathAndNotify_Works() {
        withConcurrency((contracts, concurrency) -> {
            final List<Completion<String>> completions = new ArrayList<>();
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose closeNotify = completable.notify(completions::add);
                 AutoClose closeCompletable = completable.open()) {
                ignore(closeNotify);
                ignore(closeCompletable);
                final Completion<String> completion = concurrency.createCompletion(b -> b.state(FAILED));
                completable.onCompletion(completion);
                
                assertEquals(1, completions.size());
                assertEquals(completion, completions.get(0));
            }
        });
    }
    
    @Test
    default void completable_notify_Close_Works() {
        withConcurrency((contracts, concurrency) -> {
            final List<Completion<String>> completions = new ArrayList<>();
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose closeNotify = completable.notify(completions::add);
                 AutoClose closeCompletable = completable.open()) {
                ignore(closeCompletable);
                implicitClose(closeNotify);
                final Completion<String> completion = concurrency.createCompletion(b -> b.state(FAILED));
                completable.onCompletion(completion);
                
                assertEquals(0, completions.size());
            }
        });
    }
    
    @Test
    default void completable_RedundantOrIllegalStates_AreIgnored() {
        withConcurrency((contracts, concurrency) -> {
            final List<Completion<String>> completions = new ArrayList<>();
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose closeNotify = completable.notify(completions::add);
                 AutoClose closeCompletable = completable.open()) {
                ignore(closeCompletable);
                ignore(closeNotify);
                completable.onCompletion(concurrency.createCompletion(b -> b.state(PENDING)));
                completable.onCompletion(concurrency.createCompletion(b -> b.state(PENDING)));
                completable.onCompletion(concurrency.createCompletion(b -> b.state(SUCCEEDED)));
                
                assertTrue(completable.isCompleted());
                assertTrue(completable.getCompletion().isPresent());
                assertEquals(SUCCEEDED, completable.getCompletion().get().getState());
                assertEquals(1, completions.size());
                assertEquals(SUCCEEDED, completions.get(0).getState());
            }
        });
    }
    
    @Test
    default void completable_notify_CloseTwice_Works() {
        withConcurrency((contracts, concurrency) -> {
            final Completable<String> completable = concurrency.createCompletable(b -> {
            });
            try (AutoClose closeNotify = completable.notify(c -> {})) {
                implicitClose(closeNotify);
                assertDoesNotThrow(() -> {
                    implicitClose(closeNotify);
                });
            }
        });
    }
    
    @ParameterizedTest(name = "threads = {0}")
    @ValueSource(ints = {1, 3, 17, 80})
    default void completable_Stress_Threads_Works(int numberOfThreads) {
        withConcurrency((contracts, concurrency) -> {
            final List<Completion<String>> completions = new CopyOnWriteArrayList<>();
            final Completable<String> completable = concurrency.createCompletable(b -> {});
            final CountDownLatch latch = new CountDownLatch(1);
            final CountDownLatch listeningLatch = new CountDownLatch(numberOfThreads);
            
            final Supplier<Runnable> runnableFactory = () -> () -> {
                try (AutoClose closeNotify = completable.notify(completions::add)) {
                    ignore(closeNotify);
                    listeningLatch.countDown();
                    //noinspection ResultOfMethodCallIgnored
                    sanitize(() -> latch.await(1, TimeUnit.MINUTES));
                }
            };
            
            final SpawnThreads spawnThreads = new SpawnThreads(numberOfThreads, runnableFactory);
            spawnThreads.start();
            
            //noinspection ResultOfMethodCallIgnored
            sanitize(() -> listeningLatch.await(1, TimeUnit.MINUTES));
            
            
            try (AutoClose closeCompletable = completable.open()) {
                ignore(closeCompletable);
                completable.onCompletion(concurrency.createCompletion(b -> b.state(SUCCEEDED)));
                latch.countDown();
            }
            
            spawnThreads.finish();
            assertEquals(numberOfThreads, completions.size());
        });
    }
}
