import static org.easymock.EasyMock.*;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestExecutorFactory;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;



/**
 * A little scaffolding to avoid repeating over and over the same code (but allows for variants)
 * @author Andrea Aime (TOPP)
 *
 */
public class TestRunnerScaffolding {
    TestMetadata metadata;
    TestProperties emptyProperties;
    
    
    public TestRunnerScaffolding() {
        metadata = new TestMetadata("test", "whosGonnaTestTheTests", "0.1");
        emptyProperties = new TestPropertiesImpl();
    }
    
    public void performTest() throws Throwable {
        TestExecutor executor = buildExecutor();
        TestExecutorFactory factory = buildFactory(executor);
        TestListener[] listeners = buildTestListeners(executor);
        
        // run the tests
        ConformanceTestRunner runner = new ConformanceTestRunner();
        for (TestListener testListener : listeners) {
            runner.addTestRunListener(testListener);
        }
        runner.runTests(factory);
        runner.dispose();

        // make sure all expectations are matched
        verify(factory);
        verify(executor);
        for (TestListener testListener : listeners) {
            verify(testListener);
        }
    }

    protected TestListener[] buildTestListeners(TestExecutor executor) {
        // build a listener and set expectations
        TestListener listener = createMock(TestListener.class);
        listener.testRunStarting(metadata, emptyProperties, 1);
        listener.testCallExecuted(same(executor), same(metadata),
                eq(emptyProperties), anyDouble(), eq((Throwable) null));
        listener.testRunCompleted(metadata, emptyProperties);
        replay(listener);
        return new TestListener[] {listener};
    }

    protected TestExecutorFactory buildFactory(TestExecutor executor) {
        // build a factory and set expectations
        TestExecutorFactory factory = createMock(TestExecutorFactory.class);
        expect(factory.createMetadata()).andReturn(metadata);
        expect(factory.createTestExecutor()).andReturn(executor);
        replay(factory);
        return factory;
    }

    protected TestExecutor buildExecutor() throws Throwable {
        // build an executor that does nothing (and set expectations)
        TestExecutor executor = createMock(TestExecutor.class);
        executor.run(emptyProperties);
        replay(executor);
        return executor;
    }
}