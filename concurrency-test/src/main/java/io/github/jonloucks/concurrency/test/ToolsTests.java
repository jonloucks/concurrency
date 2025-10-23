package io.github.jonloucks.concurrency.test;

import org.junit.jupiter.api.Test;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public interface ToolsTests
{
    
    @Test
    default void testTools_Instantiate_Throws() {
        assertInstantiateThrows(Tools.class);
    }
    
    @Test
    default void testTools_clean_DoesNotThrow() {
        assertDoesNotThrow(Tools::clean);
    }
    
    @Test
    default void testTools_ignore() {}
}
