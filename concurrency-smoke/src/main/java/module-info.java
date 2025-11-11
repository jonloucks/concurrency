/**
 * Includes all default components of the Concurrency library needed for a working deployment.
 */
module io.github.jonloucks.concurrency.smoke {
    requires transitive io.github.jonloucks.concurrency;
    
    uses io.github.jonloucks.contracts.api.ContractsFactory;
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    
    exports io.github.jonloucks.concurrency.smoke;
}