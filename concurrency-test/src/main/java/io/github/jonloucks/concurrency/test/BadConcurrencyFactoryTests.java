package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Concurrency;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static io.github.jonloucks.contracts.test.Tools.assertObject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("CodeBlock2Expr")
public interface BadConcurrencyFactoryTests {
    @Test
    default void badContractsFactory_HasProtectedConstructor() throws Throwable {
        final Class<?> klass = Class.forName(BadConcurrencyFactory.class.getCanonicalName());
        final Constructor<?> constructor = klass.getDeclaredConstructor();
        constructor.setAccessible(true);
        final int modifiers = constructor.getModifiers();
        
        assertFalse(Modifier.isPublic(modifiers), "constructor should not be public.");
    }
    
    @Test
    default void badContractsFactory_HasPrivateConstructor() throws Throwable {
        final BadConcurrencyFactory badContractsFactory = new BadConcurrencyFactory();
        final Concurrency.Config config = new Concurrency.Config(){};
        final Exception thrown = assertThrows(Exception.class, () -> {
            badContractsFactory.create(config);
        });
        
        assertObject(thrown);
    }
}
