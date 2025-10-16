/**
 * For internal tests specific to this module
 */
module io.github.jonloucks.concurrency.api.test {
    requires transitive io.github.jonloucks.contracts.test;
    requires transitive io.github.jonloucks.concurrency.api;
    
    opens io.github.jonloucks.concurrency.api.test to org.junit.platform.commons;
}