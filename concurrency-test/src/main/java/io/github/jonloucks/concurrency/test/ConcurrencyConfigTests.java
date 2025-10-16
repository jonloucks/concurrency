package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Concurrency.Config.Builder;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.github.jonloucks.concurrency.api.Concurrency.Config.DEFAULT;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ConcurrencyConfigTests {
    
    @Test
    default void concurrencyConfig_Defaults() {
        withConcurrency( (contracts,concurrency)-> {
            final Builder builder = contracts.claim(Builder.FACTORY).get();
            
            assertEquals(DEFAULT.useReflection(), builder.useReflection());
            assertEquals(DEFAULT.useServiceLoader(), builder.useServiceLoader());
            assertEquals(DEFAULT.contracts(), builder.contracts());
            assertEquals(DEFAULT.serviceLoaderClass(), builder.serviceLoaderClass());
            assertEquals(DEFAULT.reflectionClassName(), builder.reflectionClassName());
            assertEquals(DEFAULT.shutdownTimeout(), builder.shutdownTimeout());
        });
    }
    
    @Test
    default void concurrencyConfig_Modify() {
        withConcurrency( (contracts,concurrency)-> {
            final Builder builder = contracts.claim(Builder.FACTORY).get();
            
            builder
                .useReflection(!DEFAULT.useReflection())
                .useServiceLoader(!DEFAULT.useServiceLoader())
                .contracts(contracts)
                .serviceLoaderClass(BadConcurrencyFactory.class)
                .reflectionClassName("MyReflectionClassName")
                .shutdownTimeout(DEFAULT.shutdownTimeout().plus(Duration.ofSeconds(1)));
            
            assertEquals(!DEFAULT.useReflection(), builder.useReflection());
            assertEquals(!DEFAULT.useServiceLoader(), builder.useServiceLoader());
            assertEquals(contracts, builder.contracts());
            assertEquals(BadConcurrencyFactory.class, builder.serviceLoaderClass());
            assertEquals("MyReflectionClassName", builder.reflectionClassName());
            assertEquals(DEFAULT.shutdownTimeout().plus(Duration.ofSeconds(1)), builder.shutdownTimeout());
        });
    }
}
