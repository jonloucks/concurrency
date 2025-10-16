/**
 * Includes all default components of the Concurrency library needed for a working deployment.
 */
module io.github.jonloucks.concurrency.runtests {
    requires transitive io.github.jonloucks.concurrency.api;
    requires transitive io.github.jonloucks.concurrency.impl;
    requires transitive io.github.jonloucks.concurrency.test;
    requires transitive io.github.jonloucks.concurrency;
    
    opens io.github.jonloucks.concurrency.runtests to org.junit.platform.commons;
    
    exports io.github.jonloucks.concurrency.runtests;
}