/**
 * Includes all components for the smoke app
 */
module io.github.jonloucks.concurrency.smoke {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.concurrency;
    
    uses io.github.jonloucks.contracts.api.ContractsFactory;
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    
    exports io.github.jonloucks.concurrency.smoke;
}