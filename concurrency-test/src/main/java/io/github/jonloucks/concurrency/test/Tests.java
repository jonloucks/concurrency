package io.github.jonloucks.concurrency.test;

/**
 * All the tests for a GlobalConcurrency implementation as well as tests for testing tools.
 */
public interface Tests extends
    BadConcurrencyFactoryTests,
    ExceptionTests,
    GlobalConcurrencyTests,
    ConcurrencyConfigTests,
    ConcurrencyTests,
    ConcurrencyFactoryTests,
    IdempotentTests,
    InternalTests,
    ToolsTests
{
}
