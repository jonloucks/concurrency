package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Completable;
import io.github.jonloucks.concurrency.api.Completion;
import io.github.jonloucks.concurrency.api.Completions;
import io.github.jonloucks.concurrency.api.OnCompletion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.Completion.State.FAILED;
import static io.github.jonloucks.concurrency.api.Completion.State.SUCCEEDED;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.assertObject;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface CompletionsTests {
    @Test
    default void completable_createCompletable_WithNullBuilderConsumer_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.createCompletable((Consumer<Completable.Config.Builder<String>>)null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_createCompletable_WithNullConfig_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.createCompletable((Completable.Config<String>)null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_createCompletion_WithNullBuilderConsumer_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.createCompletion((Consumer<Completion.Config.Builder<String>>)null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_createCompletion_WithNullConfig_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.createCompletion((Completion.Config<String>)null);
            });
            assertThrown(thrown);
        });
    }
    
    @Test
    default void completable_completeLater_WithNullOnCompletion_Throws(@Mock Supplier<String> successSupplier) {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.completeNow(null, successSupplier);
            });
            assertThrown(thrown);
            verify(successSupplier, times(0)).get();
        });
    }
    
    @Test
    default void completable_completeNow_WithNullSuccessBlock_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final Error error = new Error("Problem.");
            final Supplier<String> successSupplier = () -> {throw error;};
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final Error thrown = assertThrows(Error.class, () -> {
                completions.completeNow(onCompletion, successSupplier);
            });
            assertThrown(thrown);
            assertEquals(1, completionList.size());
            final Completion<String> completion = completionList.get(0);
            assertEquals(FAILED, completion.getState());
            assertTrue(completion.isCompleted());
            assertTrue(completion.getThrown().isPresent());
            assertThrown(completion.getThrown().get());
        });
    }
    
    @Test
    default void completable_completeNow_WithHappyPath_Works() {
        withConcurrency( (contracts, concurrency) -> {
            final Supplier<String> successSupplier = () -> "success";
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            
            final String finalValue = completions.completeNow(onCompletion, successSupplier);
            
            assertEquals(1, completionList.size());
            final Completion<String> completion = completionList.get(0);
            assertEquals(SUCCEEDED, completion.getState());
            assertTrue(completion.getValue().isPresent());
            assertEquals(finalValue, completion.getValue().get());
            assertTrue(completion.isCompleted());
            assertFalse(completion.getThrown().isPresent());
        });
    }
    
    @Test
    default void completable_completeNow_WithThrowingSuccessBlock_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.completeNow(onCompletion, null);
            });
            assertThrown(thrown);
            assertEquals(1, completionList.size());
            final Completion<String> completion = completionList.get(0);
            assertEquals(FAILED, completion.getState());
            assertTrue(completion.isCompleted());
            assertTrue(completion.getThrown().isPresent());
            assertThrown(completion.getThrown().get());
        });
    }
    
    @Test
    default void completable_completeLater_WithNullOnCompletion_Throws(@Mock Consumer<OnCompletion<String>> delegate) {
        withConcurrency( (contracts, concurrency) -> {
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.completeLater(null, delegate);
            });
            assertThrown(thrown);
            verify(delegate, times(0)).accept(any());
            verify(delegate, times(0)).accept(isNull());
        });
    }
    
    @Test
    default void completable_completeLater_WithNullDelegate_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                completions.completeLater(onCompletion, null);
            });
            assertThrown(thrown);
            assertEquals(1, completionList.size());
            final Completion<String> completion = completionList.get(0);
            assertEquals(FAILED, completion.getState());
            assertTrue(completion.isCompleted());
            assertTrue(completion.getThrown().isPresent());
            assertThrown(completion.getThrown().get());
        });
    }
    
    @Test
    default void completable_completeLater_WithThrownDelegate_Throws() {
        withConcurrency( (contracts, concurrency) -> {
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            final Error error = new Error("Problem.");
            final Consumer<OnCompletion<String>> delegate = c -> {throw error;};
            final Error thrown = assertThrows(Error.class, () -> {
                completions.completeLater(onCompletion, delegate);
            });
            assertThrown(thrown);
            assertEquals(1, completionList.size());
            final Completion<String> completion = completionList.get(0);
            assertEquals(FAILED, completion.getState());
            assertTrue(completion.isCompleted());
            assertTrue(completion.getThrown().isPresent());
            assertEquals(error, completion.getThrown().get());
        });
    }
    
    @Test
    default void completable_completeLater_HappyPath_Works(@Mock Consumer<OnCompletion<String>> delegate) {
        withConcurrency( (contracts, concurrency) -> {
            final List<Completion<String>> completionList = new ArrayList<>();
            final OnCompletion<String> onCompletion = completionList::add;
            final Completions completions = contracts.claim(Completions.CONTRACT);
            completions.completeLater(onCompletion, delegate);
   
            assertEquals(0, completionList.size());

            verify(delegate, times(1)).accept(any());
            verify(delegate, times(0)).accept(isNull());
        });
    }
    
    @Test
    default void completions_create_WithDefaultBuilderConsumer_Works() {
        withConcurrency( (contracts, concurrency) -> {
            final Completions factory = contracts.claim(Completions.CONTRACT);
            final Completion<String> completion = factory.createCompletion(b -> {});

            assertObject(completion);
            assertEquals(Completion.State.DELEGATED, completion.getState());
            assertFalse(completion.getThrown().isPresent());
            assertFalse(completion.getValue().isPresent());
            assertFalse(completion.getFuture().isPresent());
            assertFalse(completion.isCompleted());
        });
    }
    
    @Test
    default void completions_create_WithBuilderConsumer_Works(@Mock Future<String> future) {
        withConcurrency( (contracts, concurrency) -> {
            final Completions factory = contracts.claim(Completions.CONTRACT);
            final Error thrown = new Error("Oh my.");

            final Completion<String> completion = factory.createCompletion(b -> {
                b.state(Completion.State.CANCELLED)
                    .thrown(thrown)
                    .value("text")
                    .future(future);
            });
            
            assertObject(completion);
            assertEquals(Completion.State.CANCELLED, completion.getState());
            assertTrue(completion.getThrown().isPresent());
            assertTrue(completion.getValue().isPresent());
            assertTrue(completion.getFuture().isPresent());
            assertTrue(completion.isCompleted());
            assertEquals(future, completion.getFuture().get());
            assertEquals(thrown, completion.getThrown().get());
            assertEquals("text", completion.getValue().get());
        });
    }
}
