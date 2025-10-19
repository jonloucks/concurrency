package io.github.jonloucks.concurrency.api;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Defines how a transition between states will be done
 *
 * @param <R> return type of the transition
 */
public interface Transition<S, R> {
    Optional<String> event();
    
    S goalState();

    Optional<Supplier<R>> action();
    
    Optional<S> errorState();
    
    boolean rethrow();
    
    Optional<Supplier<R>> orElse();

    /**
     * Responsible for building a Transition
     *
     * @param <S> the return type of the transition
     */
    interface Builder<B extends Builder<B,S,R>, S, R> extends Transition<S, R> {
        Builder<B,S,R> event(String name);
        
        Builder<B,S,R> goalState(S goalState);
        
        Builder<B,S,R> action(Supplier<R> action);
        
        Builder<B,S,R> action(Runnable action);
        
        Builder<B,S,R> errorState(S state);
        
        Builder<B,S,R> orElse(Supplier<R> orElse);
        
        Builder<B,S,R> rethrow(boolean rethrow);
    }
}
