package io.github.jonloucks.concurrency.smoke.test;

import io.github.jonloucks.concurrency.test.Tools;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(RunTests.RunExtension.class)
public final class RunTests implements SmokeTests {
    public RunTests() {
    
    }

    public static final class RunExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
        public RunExtension() {
        }

        @Override
        public void afterTestExecution(ExtensionContext extensionContext) {
            Tools.clean();
        }
        
        @Override
        public void beforeTestExecution(ExtensionContext extensionContext) {
            Tools.clean();
        }
    }

}
