# concurrency

A production-ready Java library providing advanced concurrency utilities that extend beyond the standard Java runtime.

[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11337/badge)](https://www.bestpractices.dev/projects/11337)
[![Coverage Badge](https://raw.githubusercontent.com/jonloucks/concurrency/refs/heads/badges/main-coverage.svg)](https://jonloucks.github.io/concurrency/jacoco/)
[![Javadoc Badge](https://raw.githubusercontent.com/jonloucks/concurrency/refs/heads/badges/main-javadoc.svg)](https://jonloucks.github.io/concurrency/javadoc/)

## Features

### 1. Waitable<T> - Thread-Safe Conditional Waiting
A thread-safe generic reference that allows multiple threads to wait for specific conditions. Perfect for scenarios where threads need to coordinate based on value changes.

**Key Capabilities:**
- Multiple threads can wait for different conditions simultaneously
- Predicate-based condition checking
- Timeout support
- Thread-safe value updates

**Example:**
```java
// Creating a Waitable
Waitable<String> weather = GlobalConcurrency.createWaitable("Unknown");

// Changing the value from any thread
weather.accept("Sunny");

// Waiting for a condition with timeout
Optional<String> match = weather.getWhen(
    s -> !s.contains("Rain"), 
    Duration.ofSeconds(10)
);
if (match.isPresent()) {
    goForWalk();
}
```

### 2. WaitableNotify<T> - Asynchronous Condition Notifications
Provides asynchronous callback notifications when user-defined conditions are satisfied. Instead of blocking threads, register callbacks that execute when conditions are met.

**Key Capabilities:**
- Non-blocking condition monitoring
- Multiple subscribers can listen for different conditions
- Automatic cleanup of one-time notifications
- Thread-safe callback execution

**Example:**
```java
Waitable<Integer> counter = GlobalConcurrency.createWaitable(0);

// Register callback for when counter reaches threshold
counter.notifyWhen(
    count -> count >= 10,
    value -> System.out.println("Threshold reached: " + value)
);

// Update from any thread
counter.accept(counter.get() + 1);
```

### 3. StateMachine<T> - Generic State Machine
A flexible state machine implementation with configurable transition rules, event-driven state changes, and the ability to wait for specific states.

**Key Capabilities:**
- Define custom states (works with enums or any type)
- Enforce state transition rules
- Event-driven state changes
- Execute logic during transitions
- Wait for specific state changes
- Success, error, and failed state handling

**Example:**
```java
enum ConnectionState { DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING }

// Create state machine with all possible states
StateMachine<ConnectionState> connection = GlobalConcurrency.createStateMachine(
    ConnectionState.class, 
    ConnectionState.DISCONNECTED
);

// Configure allowed transitions
connection.config(builder -> builder
    .allowTransition("connect", ConnectionState.DISCONNECTED, ConnectionState.CONNECTING)
    .allowTransition("connected", ConnectionState.CONNECTING, ConnectionState.CONNECTED)
    .allowTransition("disconnect", ConnectionState.CONNECTED, ConnectionState.DISCONNECTING)
    .allowTransition("disconnected", ConnectionState.DISCONNECTING, ConnectionState.DISCONNECTED)
);

// Execute transition with logic
connection.transition(t -> t
    .event("connect")
    .successState(ConnectionState.CONNECTING)
    .successValue(() -> openSocket())
    .errorState(ConnectionState.DISCONNECTED)
);

// Wait for connected state
Optional<ConnectionState> state = connection.getWhen(
    s -> s == ConnectionState.CONNECTED,
    Duration.ofSeconds(30)
);
```

### 4. Completable<T> & Completion<T> - Asynchronous Operation Lifecycle
Track and manage asynchronous activities from start to finish with guaranteed completion callback execution.

**Key Capabilities:**
- Track completion state (RUNNING, SUCCEEDED, FAILED, CANCELED)
- Observe state and value changes
- Guaranteed callback execution semantics
- Integration with Java Futures
- Error handling with exception tracking

**Example:**
```java
Completable<String> operation = GlobalConcurrency.createCompletable(config -> {
    // Configuration
});

// Observe completion
operation.notifyCompletion(completion -> {
    switch (completion.getState()) {
        case SUCCEEDED -> System.out.println("Result: " + completion.getValue().orElse(""));
        case FAILED -> System.out.println("Error: " + completion.getThrown().orElse(null));
        case CANCELED -> System.out.println("Operation canceled");
    }
});

// Complete the operation
operation.complete(Completion.succeeded("Done!"));
```

## Installation

### Maven
```xml
<dependency>
    <groupId>io.github.jonloucks.concurrency</groupId>
    <artifactId>concurrency</artifactId>
    <version>1.3.1</version>
</dependency>
```

### Gradle
```gradle
implementation 'io.github.jonloucks.concurrency:concurrency:1.3.1'
```

## Documentation and Reports

- [Java API Documentation](https://jonloucks.github.io/concurrency/javadoc/)
- [Test Coverage Report](https://jonloucks.github.io/concurrency/jacoco/)
- [Maven Central Repository](https://mvnrepository.com/artifact/io.github.jonloucks.concurrency/concurrency)

## Requirements

- Java 11 or higher
- No external runtime dependencies

## License

See [LICENSE](LICENSE) file for details.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines.
