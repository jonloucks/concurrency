package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.*;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The Concurrency API
 */
public interface Concurrency extends AutoOpen {
    /**
     * Access the current Concurrency implementation
     */
    Contract<Concurrency> CONTRACT = Contract.create(Concurrency.class);
    
    /**
     * Create a new Waitable with the given initial value
     *
     * @param initialValue the intial value, null is allowed
     * @return the waitable
     * @param <T> the type of waitable
     */
    <T> Waitable<T> createWaitable(T initialValue);
    
    /**
     * Create a new StateMachine
     *
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if initialState is null
     */
    <T> StateMachine<T> createStateMachine(T initialState);
    
    /**
     * Create a new StateMachine
     *
     * @param enumClass the enumeration class
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of state
     * @throws IllegalArgumentException if enumClass is null or initialState is null
     */
    <T extends Enum<T>> StateMachine<T> createStateMachine(Class<T> enumClass, T initialState);
    
    /**
     * Create a new StateMachine by configuration callback
     *
     * @param builderConsumer responsible for building the configuration
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if builderConsumer is null or resulting configuration is invalid
     */
    <T> StateMachine<T> createStateMachine(Consumer<StateMachine.Config.Builder<T>> builderConsumer);
    
    <T> Completable<T> createCompletable(Consumer<Completable.Config.Builder<T>> builderConsumer);
    
    <T> Completion<T> createCompletion(Consumer<Completion.Config.Builder<T>> builderConsumer);
    
    /**
     * The configuration used to create a new Concurrency instance.
     */
    interface Config {
        
        /**
         * The default configuration used when creating a new Concurrency instance
         */
        Config DEFAULT = new Config() {};
        
        /**
         * @return if true, reflection might be used to locate the ConcurrencyFactory
         */
        default boolean useReflection() {
            return true;
        }
        
        /**
         * @return the class name to use if reflection is used to find the ConcurrencyFactory
         */
        default String reflectionClassName() {
            return "io.github.jonloucks.concurrency.impl.ConcurrencyFactoryImpl";
        }
        
        /**
         * @return if true, the ServiceLoader might be used to locate the ConcurrencyFactory
         */
        default boolean useServiceLoader() {
            return true;
        }
        
        /**
         * @return the class name to load from the ServiceLoader to find the ConcurrencyFactory
         */
        default Class<? extends ConcurrencyFactory> serviceLoaderClass() {
            return ConcurrencyFactory.class;
        }
        
        /**
         * @return the contracts, some use case have their own Contracts instance.
         */
        default Contracts contracts() {
            return GlobalContracts.getInstance();
        }
        
        /**
         * How long to wait for logging to shut down before giving up
         *
         * @return the timeout duration
         */
        default Duration shutdownTimeout() {
            return Duration.ofSeconds(60);
        }
        
        /**
         * The Concurrency configuration
         */
        interface Builder extends Config {
            
            /**
             * Concurrency Config Builder
             */
            Contract<Supplier<Builder>> FACTORY = Contract.create("Concurrency Config Builder Factory");
            
            /**
             * @param useReflection enables or disables locating ConcurrencyFactory implementation by reflection
             * @return this builder
             */
            Builder useReflection(boolean useReflection);
            
            /**
             * @param useServiceLoader the ServiceLoader might be used to locate the ConcurrencyFactory
             * @return this builder
             */
            Builder useServiceLoader(boolean useServiceLoader);
            
            /**
             * @param contracts the Contracts to use
             * @return this builder
             */
            Builder contracts(Contracts contracts);
            
            /**
             * @param shutdownTimeout how long to wait for shutdown to complete before timing out
             * @return this builder
             */
            Builder shutdownTimeout(Duration shutdownTimeout);
            
            /**
             * @param reflectionClassName the class name to use if reflection is used to find the ContractsFactory
             * @return this builder
             */
            Builder reflectionClassName(String reflectionClassName);
            
            /**
             * @param serviceLoaderClass the class name to load from the ServiceLoader to find the ContractsFactory
             * @return this builder
             */
            Builder serviceLoaderClass(Class<? extends ConcurrencyFactory> serviceLoaderClass);
        }
    }
}
