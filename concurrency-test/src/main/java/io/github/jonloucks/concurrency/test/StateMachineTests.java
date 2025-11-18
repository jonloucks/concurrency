package io.github.jonloucks.concurrency.test;

import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import io.github.jonloucks.contracts.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.test.Tools.assumeStateMachineFactory;
import static io.github.jonloucks.concurrency.test.Tools.withConcurrency;
import static io.github.jonloucks.contracts.test.Tools.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("CodeBlock2Expr")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
            assertThrown(IllegalArgumentException.class,
                () ->  factory.create((String)null),
                "Initial state must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithNullConfig_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            assertThrown(IllegalArgumentException.class,
                () -> factory.create((StateMachine.Config<String>) null),
                "Config must be present.");
        });
    }
    
    @Test
    default void stateMachine_create_WithNullBuilderConsumer_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            assertThrown(IllegalArgumentException.class,
                () -> factory.create((Consumer<StateMachine.Config.Builder<String>>)null),
                "Builder consumer must be present.");
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
    default void stateMachine_create_BuilderWithNullStates_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            assertThrown(IllegalArgumentException.class,
                () -> factory.create(b -> b.states(null)),
                "State list must be present.");
            
        });
    }
    
    @Test
    default void stateMachine_create_BuilderWithStates_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            
            final StateMachine<String> stateMachine = factory.create(
                b -> b.states(Arrays.asList("green", "blue")).initial("blue"));
            
            assertEquals("blue", stateMachine.getState());
            assertTrue(stateMachine.hasState("blue"));
            assertTrue(stateMachine.hasState("green"));
            assertFalse(stateMachine.hasState("unknown"));
        });
    }
    
    @Test
    default void stateMachine_setState_WithUnknownState_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<String> stateMachine = factory.create("initial");
            
            assertThrown(IllegalArgumentException.class,
                () -> stateMachine.setState("go", "unknown"),
        "State must be known.");
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
                .rules(Thread.State.NEW, singletonList((s, r) -> {
                    assertNotNull(s);
                    return !Thread.State.WAITING.equals(r);
                }))
            );
            
            assertTrue(stateMachine.isTransitionAllowed("abc", Thread.State.RUNNABLE));
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.BLOCKED)); // unknown
            assertFalse(stateMachine.isTransitionAllowed("abc", Thread.State.WAITING)); // excluded by rule
        });
    }
    
    @Test
    default void stateMachine_createWithBuild_WithNullRules_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            factory.create(b -> {
                b.initial(1);
                
                assertThrown(IllegalArgumentException.class,
                    () -> b.rules(1, null),
                    "Rule list must be present.");
                
            });
        });
    }
    
    @Test
    default void stateMachine_createWithBuild_WithNullRule_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            factory.create(b -> {
                b.initial(1);
                
                assertThrown(IllegalArgumentException.class,
                    () -> b.rule(1, null),
                    "Rule must be present.");
            });
        });
    }
    
    @Test
    default void stateMachine_createWithBuild_WithRules_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Integer> stateMachine = factory.create(b -> {
                b.initial(1).states(Arrays.asList(1, 2, 3));
                
                final List<StateMachine.Rule<Integer>> rules = singletonList((s, r) -> false);
                
                assertEquals(b, b.rules(1, rules));
                assertEquals(rules, b.getStateRules(1));
            });
            assertFalse(stateMachine.isTransitionAllowed("abc", 2));
        });
    }
    
    @Test
    default void stateMachine_createWithBuild_WithRule_Works() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Integer> stateMachine = factory.create(b -> {
                b.initial(1).states(Arrays.asList(1, 2, 3));
                
                StateMachine.Rule<Integer> rule = (s, r) -> false;
                
                assertEquals(b, b.rule(1, rule));
                assertEquals(rule, b.getStateRules(1).get(0));
            });
            assertFalse(stateMachine.isTransitionAllowed("abc", 2));
        });
    }
    
    @Test
    default void stateMachine_transition_WithoutEvent_Throws() {
        withConcurrency((contracts, concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create(b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            
            assertThrown(IllegalArgumentException.class,
                () -> {
                    stateMachine.transition(b -> b
                        .successState(Thread.State.RUNNABLE)
                    );
                }, "Event must be present.");
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
    
    @ParameterizedTest
    @MethodSource("io.github.jonloucks.concurrency.test.Internal#getThrowingParameters")
    default void stateMachine_transition_SuccessThrows_Throws(Runnable throwingBlock) {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            
            assertThrown(Throwable.class, () -> {
                stateMachine.transition( b -> b
                    .event("prepare")
                    .successValue(throwingBlock)
                    .successState(Thread.State.RUNNABLE)
                );
            });
        });
    }
    
    @ParameterizedTest
    @MethodSource("io.github.jonloucks.concurrency.test.Internal#getThrowingParameters")
    default void stateMachine_transition_SuccessThrowsAndErrorValue_ReturnsValue(Runnable throwingBlock) {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(Thread.State.NEW)
                .states(Arrays.asList(Thread.State.values()))
            );
            
            final String returnValue = stateMachine.transition( b -> b
                .event("prepare")
                .successValue(throwingBlock)
                .successState(Thread.State.RUNNABLE)
                .errorValue(() -> "Error value.")
            );
            
            assertEquals("Error value.", returnValue);
        });
    }
    
    @Test
    default void stateMachine_transition_MissingGoalState_Throws() {
        withConcurrency((contracts,concurrency) -> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final StateMachine<String> stateMachine = factory.create("initial");
  
            assertThrown(IllegalArgumentException.class,
                () -> stateMachine.transition(b -> {}),
                "Rule must be present.");
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
    
    @Test
    default void stateMachine_notifyIf_WithValid_Works(@Mock Consumer<Thread.State> listener) {
        withConcurrency((contracts,concurrency)-> {
            final StateMachineFactory factory = assumeStateMachineFactory(contracts);
            final Thread.State initial = Thread.State.NEW; // avoiding first value in case that is used as default
            final StateMachine<Thread.State> stateMachine = factory.create( b -> b
                .initial(initial)
                .states(Arrays.asList(Thread.State.values())));
            
            try (AutoClose closeNotify = stateMachine.notifyIf(v -> !initial.equals(v), listener)) {
                ignore(closeNotify);
                stateMachine.setState("prepare", Thread.State.RUNNABLE);
                verify(listener, times(1)).accept(eq(Thread.State.RUNNABLE));
            }
            
            stateMachine.setState("block", Thread.State.BLOCKED);
            
            verify(listener, times(1)).accept(any());
        });
    }
}
