/**
 * The implementation module for Concurrency
 */
module io.github.jonloucks.concurrency.impl {
    requires transitive io.github.jonloucks.contracts.api;
    requires transitive io.github.jonloucks.concurrency.api;
    
    exports io.github.jonloucks.concurrency.impl;
    
    provides io.github.jonloucks.concurrency.api.ConcurrencyFactory with io.github.jonloucks.concurrency.impl.ConcurrencyFactoryImpl;
}