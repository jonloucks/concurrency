/**
 * The Test module for Concurrency
 */
module io.github.jonloucks.concurrency.test {
    requires transitive io.github.jonloucks.concurrency.api;
    requires transitive io.github.jonloucks.contracts.api;
    requires transitive io.github.jonloucks.contracts.test;
    requires org.junit.jupiter.api;
    requires org.mockito.junit.jupiter;
    requires org.mockito;
    
    opens io.github.jonloucks.concurrency.test to org.junit.platform.commons;
    exports io.github.jonloucks.concurrency.test;
}