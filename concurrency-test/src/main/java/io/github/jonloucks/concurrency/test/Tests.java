package io.github.jonloucks.concurrency.test;

/**
 * All the tests for a GlobalConcurrency implementation as well as tests for testing tools.
 */
public interface Tests extends
    BadConcurrencyFactoryTests,
    ChecksTests,
    ConstantsTests,
    ExceptionTests,
    GlobalConcurrencyTests,
    CompletionsTests,
    CompletableTests,
    CompletionStateTests,
    ConcurrencyConfigTests,
    ConcurrencyTests,
    ConcurrencyFactoryTests,
    IdempotentTests,
    InternalTests,
    StateMachineTests,
    ToolsTests,
    ValidateTests,
    WaitableTests
{
}
