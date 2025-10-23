package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.github.jonloucks.contracts.test.Tools.assertIsSerializable;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ThrowableNotThrown", "CodeBlock2Expr"})
public interface ExceptionTests {
    
    @Test
    default void exception_rethrow_WithNullThrown_Throws() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            ConcurrencyException.rethrow(null);
        });
        
        assertThrown(thrown, "Thrown must be present.");
    }
 
    @Test
    default void exception_rethrow_RuntimeException_ThrowsOriginal() {
        final RuntimeException problem = new RuntimeException("Oh My.");
        final RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            throw ConcurrencyException.rethrow(problem);
        });
        
        assertSame(problem, thrown);
    }
    
    @Test
    default void exception_rethrow_Error_ThrowsOriginal() {
        final Error problem = new Error("Oh My.");
        final Error thrown = assertThrows(Error.class, () -> {
            throw ConcurrencyException.rethrow(problem);
        });
        
        assertSame(problem, thrown);
    }
    
    @Test
    default void exception_rethrow_Unchecked_ThrowsConcurrencyException() {
        final IOException problem = new IOException("Oh My.");
        final ConcurrencyException thrown = assertThrows(ConcurrencyException.class, () -> {
            throw ConcurrencyException.rethrow(problem);
        });
        
        assertThrown(thrown, problem,"Oh My.");
    }

    @Test
    default void exception_ConcurrencyException_WithNullMessage_Throws() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new ConcurrencyException(null);
        });
        
        assertThrown(thrown);
    }
    
    @Test
    default void exception_ConcurrencyException_IsSerializable() {
        assertIsSerializable(ConcurrencyException.class);
    }
    
    @Test
    default void exception_ConcurrencyException_WithValid_Works() {
        final ConcurrencyException exception = new ConcurrencyException("Abc.");
        
        assertThrown(exception, "Abc.");
    }
    
    @Test
    default void exception_ConcurrencyException_WithCause_Works() {
        final OutOfMemoryError cause = new OutOfMemoryError("Out of memory.");
        final ConcurrencyException exception = new ConcurrencyException("Abc.", cause);
        
        assertThrown(exception, cause, "Abc.");
    }
}
