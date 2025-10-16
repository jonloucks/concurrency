/**
 * module-impl tests
 */
module io.github.jonloucks.concurrency.impl.test {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.contracts.test;
    requires transitive io.github.jonloucks.concurrency.api;
    requires transitive io.github.jonloucks.concurrency.test;
    requires transitive io.github.jonloucks.concurrency.impl;

    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    
    opens io.github.jonloucks.concurrency.impl.test to org.junit.platform.commons;
}