package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.concurrency.api.*;
import org.junit.jupiter.params.provider.Arguments;
import org.opentest4j.TestAbortedException;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.findConcurrencyFactory;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SuppressWarnings("CodeBlock2Expr")
public final class Tools {
    
    public static void clean() {
        io.github.jonloucks.contracts.test.Tools.clean();
        sanitize(()-> {
            final Concurrency.Config config = new Concurrency.Config() {};
            if (config.useServiceLoader()) {
                final ServiceLoader<? extends ConcurrencyFactory> loader = ServiceLoader.load(config.serviceLoaderClass());
                loader.reload();
            }
        });
    }
    
    public static void withConcurrency(Consumer<Concurrency.Config.Builder> builderConsumer, BiConsumer<Contracts,Concurrency> block) {
        withContracts(contracts -> {
            final ConcurrencyFactory factory = getConcurrencyFactory();
            final Concurrency concurrency = factory.create(b -> {
                b.contracts(contracts);
                builderConsumer.accept(b);
            });
            try (AutoClose closeConcurrency = concurrency.open()) {
                ignore(closeConcurrency);
                block.accept(contracts, concurrency);
            }
        });
    }
    
    static Stream<Arguments> getThrowingParameters() {
        return Stream.of(
            Arguments.of((Runnable) () -> {
                throw new Error("Error.");
            }),
            Arguments.of((Runnable) () -> {
                    throw new RuntimeException("RuntimeException.");
                }),
            Arguments.of((Runnable) () -> {
                throw new ConcurrencyException("ConcurrencyException.");
            })
        );
    }

    public static void withConcurrency(BiConsumer<Contracts,Concurrency> block) {
        withConcurrency( b->{}, block);
    }
    
    public static ConcurrencyFactory getConcurrencyFactory() {
        return getConcurrencyFactory(Concurrency.Config.DEFAULT);
    }
    
    public static ConcurrencyFactory getConcurrencyFactory(Concurrency.Config config) {
        return findConcurrencyFactory(config)
            .orElseThrow(() -> new TestAbortedException("Concurrency Factory not found."));
    }
    
    public static void withConcurrencyInstalled(Consumer<Contracts> block) {
        withConcurrency(b -> {}, (contracts, concurrency) -> {
            block.accept(contracts);
        });
    }
    
    public static <T extends Enum<T>& StateMachine.Rule<T>> void assertTransitions(Class<T> type, T from, List<T> allowList) {
        for (T to : type.getEnumConstants()) {
            if (allowList.contains(to)) {
                assertTrue(from.canTransition("unnamed", to), "Expected transition to " + to);
            } else {
                assertFalse(from.canTransition("unnamed", to), "Unexpected transition to " + to);
            }
        }
    }
    
    public static StateMachineFactory assumeStateMachineFactory(Contracts contracts) {
        assumeTrue(contracts.isBound(StateMachineFactory.CONTRACT), "StateMachineFactory is assumed");
        return contracts.claim(StateMachineFactory.CONTRACT);
    }
    
    /**
     * Utility class instantiation protection
     */
    private Tools() {
        throw new AssertionError("Illegal constructor call.");
    }
}
