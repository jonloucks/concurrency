/**
 * The Test module for Concurrency
 */
module io.github.jonloucks.concurrency.test {
    requires transitive io.github.jonloucks.concurrency.api;
    requires transitive io.github.jonloucks.contracts.api;
    requires transitive io.github.jonloucks.contracts.test;
    
    opens io.github.jonloucks.concurrency.test to org.junit.platform.commons;
    exports io.github.jonloucks.concurrency.test;
}