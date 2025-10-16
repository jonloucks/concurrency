package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.concurrency.api.*;
import org.opentest4j.TestAbortedException;

import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.github.jonloucks.contracts.test.Tools.sanitize;
import static io.github.jonloucks.contracts.test.Tools.withContracts;
import static io.github.jonloucks.concurrency.api.GlobalConcurrency.findConcurrencyFactory;

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
    
    static String uniqueString() {
        return UUID.randomUUID().toString();
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
    
    @SuppressWarnings("EmptyMethod")
    public static void ignore(Object ignored) {
    }

    /**
     * Utility class instantiation protection
     */
    private Tools() {
        throw new AssertionError("Illegal constructor call.");
    }
}
