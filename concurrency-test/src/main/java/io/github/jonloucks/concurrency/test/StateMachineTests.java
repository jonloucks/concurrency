package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
public interface StateMachineTests {
    
    @Test
    default void stateMachine_create_WIthNullInitial_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                factory.create(null);
            });
            assertThrown(thrown, "State must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WIthInitial_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<String> stateMachine = factory.create("initial");
            
            assertObject(stateMachine);
            assertEquals("initial", stateMachine.getState());
            assertFalse(stateMachine.setState("self", "initial"));
            assertTrue(stateMachine.hasState("initial"));
            assertFalse(stateMachine.hasState("unknown"));
        });
    }
    
    @Test
    default void stateMachine_isTransitionAllowed_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Thread.State> stateMachine = factory.create(Thread.State.NEW);
            
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.RUNNABLE));
        });
    }
    
    @Test
    default void stateMachine_transition_WithoutEvent_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Thread.State> stateMachine = factory.create(Thread.State.class, Thread.State.NEW);
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                stateMachine.transition(b -> b
                    .goalState(Thread.State.RUNNABLE)
                );
            });
            assertThrown(thrown, "Event must be present.");
        });
    }
    
    @Test
    default void stateMachine_transition_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<Thread.State> stateMachine = factory.create(Thread.State.class, Thread.State.NEW);
        
            stateMachine.transition( b -> b
                .event("prepare")
                .goalState(Thread.State.RUNNABLE)
            );
       
            assertEquals(Thread.State.RUNNABLE, stateMachine.getState());
        });
    }
    
    @Test
    default void stateMachine_transition_MissingGoalState_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final StateMachine<String> stateMachine = factory.create("initial");
  
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
               stateMachine.transition(b -> {});
            });
            
            assertThrown(thrown, "State must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithEnum_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = contracts.claim(StateMachineFactory.CONTRACT);
            final Thread.State initial = Thread.State.WAITING; // avoiding first value in case that is used as default
            final StateMachine<Thread.State> stateMachine = factory.create(Thread.State.class, initial);
            
            assertEquals(initial, stateMachine.getState());
            for (Thread.State state : Thread.State.values()) {
                assertTrue(stateMachine.hasState(state));
                if (stateMachine.getState() != state) {
                    assertTrue(stateMachine.setState("event", state));
                    assertEquals(state, stateMachine.getState());
                }
            }
        });
    }
}
