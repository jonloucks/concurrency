package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.test.Tools.assumeStateMachineFactory;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CodeBlock2Expr")
public interface StateMachineTests {
    
    @Test
    default void stateMachine_StateMachineFactory_Exists() {
        withConcurrency((contracts,concurrency) -> {
            assertTrue(contracts.isBound(StateMachineFactory.CONTRACT), "StateMachineFactory is required.");
        });
    }
    
    @Test
    default void stateMachine_create_WithNullInitial_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                factory.create((String)null);
            });
            assertThrown(thrown, "Initial state must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithNullConfig_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                factory.create((StateMachine.Config<String>)null);
            });
            assertThrown(thrown, "Config must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithNullBuilderConsumer_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                factory.create((Consumer<StateMachine.Config.Builder<String>>)null);
            });
            assertThrown(thrown, "Builder consumer must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithInitial_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
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
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create(b -> b
                .initial(Thread.State.NEW)
                .state(Thread.State.RUNNABLE)
                .state(Thread.State.WAITING)
                .rule(Thread.State.NEW, (s,r) -> {
                    return !Thread.State.WAITING.equals(r);
                })
            );
            
            assertTrue(stateMachine.isTransitionAllowed("abc", Thread.State.RUNNABLE));
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.BLOCKED)); // unknown
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.WAITING)); // excluded by rule
        });
    }
    
    @Test
    default void stateMachine_isTransitionAllowed_WithRules_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create(b -> b
                .initial(Thread.State.NEW)
                .state(Thread.State.RUNNABLE)
                .state(Thread.State.WAITING)
                .rules(Thread.State.NEW, List.of((s, r) -> {
                    return !Thread.State.WAITING.equals(r);
                }))
            );
            
            assertTrue(stateMachine.isTransitionAllowed("abc", Thread.State.RUNNABLE));
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.BLOCKED)); // unknown
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.WAITING)); // excluded by rule
        });
    }
    
    @Test
    default void stateMachine_transition_WithoutEvent_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                stateMachine.transition(b -> b
                    .successState(Thread.State.RUNNABLE)
                );
            });
            assertThrown(thrown, "Event must be present.");
        });
    }
    
    @Test
    default void stateMachine_transition_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            stateMachine.transition( b -> b
                .event("prepare")
                .successState(Thread.State.RUNNABLE)
            );
       
            assertEquals(Thread.State.RUNNABLE, stateMachine.getState());
        });
    }
    
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    default void stateMachine_transition_Modify_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            final String event = "prepare";
            final Thread.State successState = Thread.State.RUNNABLE;
            final Thread.State deniedState = Thread.State.BLOCKED;
            final Thread.State errorState = Thread.State.TERMINATED;
            stateMachine.transition( b -> {
                b.event("before")
                        .event(null)
                        .event(event)
                        .successState(Thread.State.NEW)
                        .successState(null)
                        .successState(successState)
                        .failedState(Thread.State.NEW)
                        .failedState(null)
                        .failedState(deniedState)
                        .errorState(Thread.State.NEW)
                        .errorState(null)
                        .errorState(errorState)
                        .successValue((Runnable)null)
                        .successValue((Supplier<Object>) null);
                
                    assertEquals(successState, b.getSuccessState());
                    assertEquals(deniedState, b.getFailedState().get());
                    assertEquals(errorState, b.getErrorState().get());
                }
            );
        });
    }
    
    @Test
    default void stateMachine_transition_SuccessThrowsError_ThrowsError() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            final Error error = new Error("Oh my.");
            
            final Throwable thrown = assertThrows(Throwable.class, () -> {
                stateMachine.transition( b -> b
                    .event("prepare")
                    .successValue( () -> {
                        throw error;
                    })
                    .successState(Thread.State.RUNNABLE)
                );
            });
   
            assertEquals(error, thrown);
            assertThrown(thrown);
        });
    }
    
    @Test
    default void stateMachine_transition_MissingGoalState_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<String> stateMachine = factory.create("initial");
  
            final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
               stateMachine.transition(b -> {});
            });
            
            assertThrown(thrown, "Rule must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithEnum_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final Thread.State initial = Thread.State.WAITING; // avoiding first value in case that is used as default
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(initial)
                .states(Arrays.asList(Thread.State.values()))
            );
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
