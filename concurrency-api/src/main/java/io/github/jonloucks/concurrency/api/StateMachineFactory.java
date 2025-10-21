package io.github.jonloucks.concurrency.api;

import io.github.jonloucks.concurrency.api.StateMachine.Config;
import io.github.jonloucks.concurrency.api.StateMachine.Config.Builder;
import io.github.jonloucks.contracts.api.Contract;
import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Arrays.asList;

/**
 * Rule machine, containing a set of sets that can be transitioned to by events/actions
 */
public interface StateMachineFactory {
    
    /**
     * The StateMachineFactory Contract
     */
    Contract<StateMachineFactory> CONTRACT = Contract.create(StateMachineFactory.class);
    
    /**
     * Create a new StateMachine by configuration
     *
     * @param config the configuration
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if config is null or configuration is invalid
     */
    <T> StateMachine<T> create(Config<T> config);
    
    /**
     * Create a new StateMachine by configuration callback
     *
     * @param builderConsumer responsible for building the configuration
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if builderConsumer is null or resulting configuration is invalid
     */
    <T> StateMachine<T> create(Consumer<Builder<T>> builderConsumer);
    
    /**
     * Create a new StateMachine
     *
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if initialState is null
     */
    default <T> StateMachine<T> create(T initialState) {
        return create(b -> b.initial(initialState));
    }
    
    /**
     * Create a new StateMachine from an Enum class
     *
     * @param enumClass the enum class
     * @param initialState the initial state
     * @return the new StateMachine
     * @param <T> the type of each state
     * @throws IllegalArgumentException if enumClass is null or initialState is null
     */
    default <T extends Enum<T>> StateMachine<T> create(Class<T> enumClass, T initialState) {
        final Class<T> validEnumClass = nullCheck(enumClass, "Enum class must be present.");
        return create( b -> b
            .initial(initialState)
            .states(asList(validEnumClass.getEnumConstants())));
    }
}
