# concurrency
### Java Concurrency Library, providing features not in the Java runtime
#### 1. Waitable, a thread safe generic reference, supporting multiple threads waiting for different conditions.
#### 2. StateMachine, a generic state machine, supporting states, rules, triggers, and waiting for state changes.


##### Waitable example:
```
// Creating a Waitable
Waitable<String> weather = GlobalConcurrency.createWaitable("Unknown");

// Changing the value
weather.accept("Sunny");

// Waiting for a condition
final Optional<String> match = weather.waitFor(s -> !s.contains("Rain"), Duration.ofSeconds(10));
if (match.isPresent()) {
    goForWalk();
}
```

## Documentation and Reports
[Java API](https://jonloucks.github.io/concurrency/javadoc/)

[Java Test Coverage](https://jonloucks.github.io/concurrency/jacoco/)

## Badges
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11337/badge)](https://www.bestpractices.dev/projects/11337)
[![Coverage Badge](https://raw.githubusercontent.com/jonloucks/concurrency/refs/heads/badges/main-coverage.svg)](https://jonloucks.github.io/concurrency/jacoco/)
[![Javadoc Badge](https://raw.githubusercontent.com/jonloucks/concurrency/refs/heads/badges/main-javadoc.svg)](https://jonloucks.github.io/concurrency/javadoc/)
