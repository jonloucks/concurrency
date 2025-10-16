/**
 * Includes all default components of the Concurrency library needed for a working deployment.
 */
module io.github.jonloucks.concurrency {
    requires transitive io.github.jonloucks.concurrency.api;
    requires transitive io.github.jonloucks.concurrency.impl;

    exports io.github.jonloucks.concurrency;
}