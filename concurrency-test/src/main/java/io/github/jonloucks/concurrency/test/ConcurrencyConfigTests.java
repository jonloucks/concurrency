package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Concurrency.Config.Builder;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Function;

import static io.github.jonloucks.concurrency.api.Concurrency.Config.DEFAULT;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static org.junit.jupiter.api.Assertions.*;

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

            final Function<Builder,Builder> assertBuilder = b -> {
                assertSame(builder, b);
                return builder;
            };
            
            assertBuilder.apply(builder.useReflection(!DEFAULT.useReflection()));
            assertBuilder.apply(builder.useServiceLoader(!DEFAULT.useServiceLoader()));
            assertBuilder.apply(builder.contracts(contracts));
            assertBuilder.apply(builder.serviceLoaderClass(BadConcurrencyFactory.class));
            assertBuilder.apply(builder.reflectionClassName("MyReflectionClassName"));
            assertBuilder.apply(builder.shutdownTimeout(DEFAULT.shutdownTimeout().plus(Duration.ofSeconds(1))));
            
            assertEquals(!DEFAULT.useReflection(), builder.useReflection());
            assertEquals(!DEFAULT.useServiceLoader(), builder.useServiceLoader());
            assertEquals(contracts, builder.contracts());
            assertEquals(BadConcurrencyFactory.class, builder.serviceLoaderClass());
            assertEquals("MyReflectionClassName", builder.reflectionClassName());
            assertEquals(DEFAULT.shutdownTimeout().plus(Duration.ofSeconds(1)), builder.shutdownTimeout());
        });
    }
}
