package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.Optional;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * Globally shared Concurrency singleton
 */
public final class GlobalConcurrency {
    
    public static <T> Waitable<T> createWaitable(T initialValue) {
        return INSTANCE.concurrency.createWaitable(initialValue);
    }
    
    /**
     * Return the global instance of Contracts
     * @return the instance
     */
    public static Concurrency getInstance() {
        return INSTANCE.concurrency;
    }
    
    /**
     * @param config the Concurrency configuration
     * @return the new Concurrency
     * @see ConcurrencyFactory#create(Concurrency.Config)
     * Note: Services created from this method are destink any that used internally
     * <p>
     * Caller is responsible for invoking open() before use and close when no longer needed
     * </p>
     */
    public static Concurrency createConcurrency(Concurrency.Config config) {
        final ConcurrencyFactory factory = findConcurrencyFactory(config)
            .orElseThrow(() -> new ConcurrencyException("Concurrency factory must be present."));
   
        return nullCheck(factory.create(config), "Concurrency could not be created.");
    }
    
    /**
     * Finds the ConcurrencyFactory implementation
     * @param config the configuration used to find the factory
     * @return the factory if found
     */
    public static Optional<ConcurrencyFactory> findConcurrencyFactory(Concurrency.Config config) {
        return new ConcurrencyFactoryFinder(config).find();
    }
    
    private GlobalConcurrency() {
        this.concurrency = createConcurrency(Concurrency.Config.DEFAULT);
        this.close = concurrency.open();
    }
    
    private static final GlobalConcurrency INSTANCE = new GlobalConcurrency();
    
    private final Concurrency concurrency;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final AutoClose close;
}
