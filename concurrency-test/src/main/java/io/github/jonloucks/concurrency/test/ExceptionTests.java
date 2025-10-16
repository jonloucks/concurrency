package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.ConcurrencyException;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertIsSerializable;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"ThrowableNotThrown", "CodeBlock2Expr"})
public interface ExceptionTests {
    
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
