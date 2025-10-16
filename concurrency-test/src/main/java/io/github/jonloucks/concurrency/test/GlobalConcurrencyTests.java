package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyException;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;
import io.github.jonloucks.concurrency.api.GlobalConcurrency;
import io.github.jonloucks.contracts.api.GlobalContracts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public interface GlobalConcurrencyTests {
    
    @Test
    default void globalConcurrency_Instantiate_Throws() {
        assertInstantiateThrows(GlobalConcurrency.class);
    }
    
    @Test
    default void globalConcurrency_getInstance_Works() {
        assertObject(GlobalConcurrency.getInstance());
    }

    @Test
    default void globalConcurrency_DefaultConfig() {
        final Concurrency.Config config = new Concurrency.Config() {
        };
        
        assertAll(
            () -> assertTrue(config.useReflection(), "config.useReflection() default."),
            () -> assertTrue(config.useServiceLoader(), "config.useServiceLoader() default."),
            () -> assertNotNull(config.reflectionClassName(), "config.reflectionClassName() was null."),
            () -> assertEquals(ConcurrencyFactory.class, config.serviceLoaderClass(), "config.serviceLoaderClass() default."),
            () -> assertEquals(GlobalContracts.getInstance(), config.contracts(), "config.contracts()  default."),
            () -> assertEquals(Duration.ofSeconds(60), config.shutdownTimeout(), "config.shutdownTimeout() default.")
        );
    }
    
    @ParameterizedTest
    @MethodSource("io.github.jonloucks.concurrency.test.GlobalConcurrencyTests$GlobalConcurrencyTestsTools#validConfigs")
    default void globalConcurrency_findConcurrencyFactory(Concurrency.Config config) {
        final Optional<ConcurrencyFactory> factory = GlobalConcurrency.findConcurrencyFactory(config);
        
        assertTrue(factory.isPresent());
        assertObject(factory.get());
    }
    
    @ParameterizedTest
    @MethodSource("io.github.jonloucks.concurrency.test.GlobalConcurrencyTests$GlobalConcurrencyTestsTools#invalidConfigs")
    default void globalConcurrency_createConcurrency_Invalid(Concurrency.Config config) {
        final ConcurrencyException thrown = assertThrows(ConcurrencyException.class, () -> {
            GlobalConcurrency.createConcurrency(config);
        });
        
        assertThrown(thrown);
    }
    
    @ParameterizedTest
    @MethodSource("io.github.jonloucks.concurrency.test.GlobalConcurrencyTests$GlobalConcurrencyTestsTools#invalidConfigs")
    default void globalConcurrency_SadPath(Concurrency.Config config) {
        final ConcurrencyException thrown = assertThrows(ConcurrencyException.class, () -> {
            GlobalConcurrency.createConcurrency(config);
        });
        
        assertThrown(thrown);
    }
    
    @Test
    default void globalConcurrency_InternalCoverage() {
        assertInstantiateThrows(GlobalConcurrencyTestsTools.class);
    }
    
    final class GlobalConcurrencyTestsTools {
        private GlobalConcurrencyTestsTools() {
            throw new AssertionError("Illegal constructor.");
        }
        
        @SuppressWarnings("RedundantMethodOverride")
        static Stream<Arguments> validConfigs() {
            return Stream.of(
                Arguments.of(new Concurrency.Config() {
                }),
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return false;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return true;
                    }
                }),
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return true;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return false;
                    }
                })
            );
        }
        
        @SuppressWarnings("RedundantMethodOverride")
        static Stream<Arguments> invalidConfigs() {
            return Stream.of(
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return false;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return false;
                    }
                }),
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return true;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return false;
                    }
                    
                    @Override
                    public Class<? extends ConcurrencyFactory> serviceLoaderClass() {
                        return BadConcurrencyFactory.class;
                    }
                }),
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return false;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return true;
                    }
                    
                    @Override
                    public String reflectionClassName() {
                        return BadConcurrencyFactory.class.getName();
                    }
                }),
                Arguments.of(new Concurrency.Config() {
                    @Override
                    public boolean useServiceLoader() {
                        return false;
                    }
                    
                    @Override
                    public boolean useReflection() {
                        return true;
                    }
                    
                    @Override
                    public String reflectionClassName() {
                        return "";
                    }
                })
            );
        }
    }
}
