package io.github.jonloucks.concurrency.smoke.test;

import io.github.jonloucks.concurrency.smoke.Main;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SmokeTests {
    
    @Test
    default void smoke_main() {
        final AtomicInteger code = new AtomicInteger(Integer.MIN_VALUE/2);
        final IntConsumer savedSystemExit = Main.setSystemExit(code::set);
        try {
            Main.main(new String[]{});
            assertEquals(0, code.get());
        } finally {
            Main.setSystemExit(savedSystemExit);
        }
    }
    
    @Test
    default void smoke_main_Failure() {
        final AtomicInteger code = new AtomicInteger(Integer.MIN_VALUE/2);
        final IntConsumer savedSystemExit = Main.setSystemExit(i -> {
            code.set(i);
            if (i == 0) {
                throw new RuntimeException("Pretend failure.");
            }
        });
        try {
            Main.main(new String[]{});
            assertEquals(1, code.get());
        } finally {
            Main.setSystemExit(savedSystemExit);
        }
    }
}
