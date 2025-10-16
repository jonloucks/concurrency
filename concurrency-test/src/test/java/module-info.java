/**
 * Module to run tests on the test tools
 */
module io.github.jonloucks.concurrency.test.run {
    requires transitive io.github.jonloucks.concurrency.test;
    
    opens io.github.jonloucks.concurrency.test.run to org.junit.platform.commons;
}