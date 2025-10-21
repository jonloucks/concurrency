package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.Optional;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * Globally shared Concurrency singleton
 */
public final class GlobalConcurrency {
    
    /**
     * Create a new Waitable with the given initial value
     *
     * @param initialValue (null is not allowed)
     * @return the waitable
     * @param <T> the type of waitable
     * @throws IllegalArgumentException if initialValue is null
     */
    public static <T> Waitable<T> createWaitable(T initialValue) {
        return INSTANCE.concurrency.createWaitable(initialValue);
    }
    
    /**
     * Create a new StateMachine
     *
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if initialState is null
     */
    public static <T> StateMachine<T> createStateMachine(T initialState) {
        return INSTANCE.concurrency.createStateMachine(initialState);
    }
    
    /**
     * Create a new StateMachine
     *
     * @param enumClass the enumeration class
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of state
     * @throws IllegalArgumentException if enumClass is null or initialState is null
     */
    public static <T extends Enum<T>> StateMachine<T> createStateMachine(Class<T> enumClass, T initialState) {
        return INSTANCE.concurrency.createStateMachine(enumClass, initialState);
    }
    
    /**
     * Return the global instance of Contracts
     * @return the instance
     */
    public static Concurrency getInstance() {
        return INSTANCE.concurrency;
    }
    
    /**
     * Create a new Concurrency instance for customized deployments.
     * Note: GlobalConcurrency has everything feature, this api
     * allows creation of more than once instance of Concurrency.
     *
     * @param config the Concurrency configuration
     * @return the new Concurrency
     * @see ConcurrencyFactory#create(Concurrency.Config)
     * Note: Services created from this method are destink any that used internally
     * <p>
     * Caller is responsible for invoking open() before use and close when no longer needed
     * </p>
     * @throws IllegalArgumentException if config is null or invalid
     */
    public static Concurrency createConcurrency(Concurrency.Config config) {
        final ConcurrencyFactory factory = findConcurrencyFactory(config)
            .orElseThrow(() -> new ConcurrencyException("Concurrency factory must be present."));
   
        return nullCheck(factory.create(config), "Concurrency could not be created.");
    }
    
    /**
     * Finds the ConcurrencyFactory implementation
     *
     * @param config the configuration used to find the factory
     * @return the factory if found
     * @throws IllegalArgumentException if config is null or invalid
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
