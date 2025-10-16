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
         * @return the timeout duration
         */
        default Duration shutdownTimeout() {
            return Duration.ofSeconds(60);
        }
        
        interface Builder extends Config {
            Contract<Supplier<Builder>> FACTORY = Contract.create("Concurrency Config Builder Factory");
            
            Builder useReflection(boolean useReflection);
            Builder useServiceLoader(boolean useServiceLoader);
            Builder contracts(Contracts contracts);
            Builder shutdownTimeout(Duration shutdownTimeout);
            Builder reflectionClassName(String reflectionClassName);
            Builder serviceLoaderClass(Class<? extends ConcurrencyFactory> serviceLoaderClass);
        }
    }
}
