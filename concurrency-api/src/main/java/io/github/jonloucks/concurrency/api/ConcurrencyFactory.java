package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.contracts.api.Repository;

import java.util.function.Consumer;

/**
 * Responsible for creating new instances of Concurrency
 */
public interface ConcurrencyFactory {
    /**
     * Used to promise and claim the ConcurrencyFactory implementation
     */
    Contract<ConcurrencyFactory> CONTRACT = Contract.create(ConcurrencyFactory.class);
    
    /**
     * Create a new instance of Concurrency
     * <p>
     *     Note: caller is responsible for calling {@link AutoOpen#open()} and calling
     *     the {@link AutoClose#close() when done}
     * </p>
     * @param config the Concurrency configuration for the new instance
     * @return the new Concurrency instance
     */
    Concurrency create(Concurrency.Config config);
    
    /**
     * Create a new instance of Concurrency
     * @param builderConsumer the config builder consumer callback
     * @return the new Concurrency instance
     */
    Concurrency create(Consumer<Concurrency.Config.Builder> builderConsumer);
    
    /**
     * Install all the requirements and promises to the given Contracts Repository.
     * Include Concurrency#CONTRACT which will private a unique
     * @param config the Concurrency config
     * @param repository the repository to add requirements and promises to
     */
    void install(Concurrency.Config config, Repository repository);
}
