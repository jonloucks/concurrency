/**
 * The API module for Concurrency
 */
module io.github.jonloucks.concurrency.api {
    requires transitive io.github.jonloucks.contracts.api;
    
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    
    exports io.github.jonloucks.concurrency.api;
}