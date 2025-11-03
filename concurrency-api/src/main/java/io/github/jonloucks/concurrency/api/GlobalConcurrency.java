package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;

/**
 * Globally shared Concurrency singleton
 */
public final class GlobalConcurrency {
    
    /**
     * Create a new Waitable with the given initial value
     *
     * @param initialValue (null is allowed)
     * @return the waitable
     * @param <T> the type of waitable
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
     * Create a new Completable
     *
     * @param builderConsumer receives the Completable Config Builder
     * @return the new Completable
     * @param <T> the type of completion value
     */
    public static <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer) {
        return INSTANCE.concurrency.createCompletable(builderConsumer);
    }
    
    /**
     * Create a new Completion
     *
     * @param builderConsumer receives the Completion Config Builder
     * @return the new Completable
     * @param <T> the type of completion value
     */
    public static <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer) {
        return INSTANCE.concurrency.createCompletion(builderConsumer);
    }
    
    /**
     * Create a new Completion
     *
     * @param config the Completion configuration
     * @return the new Completable
     * @param <T> the type of completion value
     */
    public static <T> Completion<T> createCompletion(Completion.Config<T> config) {
        return INSTANCE.concurrency.createCompletion(config);
    }
    
    /**
     * Guaranteed execution: complete later block.
     * Either the delegate successfully takes ownership of the OnCompletion or
     * a final {@link io.github.jonloucks.concurrency.api.Completion.State#FAILED} completion is dispatched
     *
     * @param onCompletion the OnCompletion callback
     * @param delegate the intended delegate to receive the OnCompletion
     * @param <T> the completion value type
     */
    public static <T> void completeLater(OnCompletion<T> onCompletion, Consumer<OnCompletion<T>> delegate) {
        INSTANCE.concurrency.completeLater(onCompletion, delegate);
    }
    
    /**
     * Guaranteed execution: complete now block
     * When this method finishes, it is guaranteed the OnCompletion will have received a final completion.
     * Exceptions will result in a {@link io.github.jonloucks.concurrency.api.Completion.State#FAILED} completion
     * Exceptions will be rethrown.
     *
     * @param onCompletion the OnCompletion callback
     * @param successBlock executed to determine the final completion value for an activity
     * @return the final completion value
     * @param <T> the completion value type
     */
    public static <T> T completeNow(OnCompletion<T> onCompletion, Supplier<T> successBlock) {
        return INSTANCE.concurrency.completeNow(onCompletion, successBlock);
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
