package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.*;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * The Concurrency API
 */
public interface Concurrency extends AutoOpen {
    /**
     * Access the current Concurrency implementation
     */
    Contract<Concurrency> CONTRACT = Contract.create(Concurrency.class);
    
    <T> Waitable<T> createWaitable(T initialValue);
    
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
             * @return if true, reflection might be used to locate the ConcurrencyFactory
             */
            Builder useReflection(boolean useReflection);
            
            /**
             * @return if true, the ServiceLoader might be used to locate the ConcurrencyFactory
             */
            Builder useServiceLoader(boolean useServiceLoader);
            
            /**
             * @return the Contracts to be used
             */
            Builder contracts(Contracts contracts);
            
            /**
             * How long to wait for shutdown to complete before timing out
             * @param shutdownTimeout the shutdown timeout
             * @return the shutdown timeout
             */
            Builder shutdownTimeout(Duration shutdownTimeout);
            
            /**
             * @return the class name to use if reflection is used to find the ContractsFactory
             */
            Builder reflectionClassName(String reflectionClassName);
            
            /**
             * @return the class name to load from the ServiceLoader to find the ContractsFactory
             */
            Builder serviceLoaderClass(Class<? extends ConcurrencyFactory> serviceLoaderClass);
        }
    }
}
