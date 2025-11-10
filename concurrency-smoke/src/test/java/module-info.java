/**
 * Includes all default components of the Concurrency library needed for a working deployment.
 */
module io.github.jonloucks.concurrency.smoke.test {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.contracts.test;
    requires transitive io.github.jonloucks.concurrency;
    requires transitive io.github.jonloucks.concurrency.test;
    requires transitive io.github.jonloucks.concurrency.smoke;

    opens io.github.jonloucks.concurrency.smoke.test to org.junit.platform.commons;
    
    exports io.github.jonloucks.concurrency.smoke.test;
}