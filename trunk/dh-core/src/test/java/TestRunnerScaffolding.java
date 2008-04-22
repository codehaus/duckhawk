import static org.easymock.EasyMock.*;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestExecutorFactory;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestRunner;



/**
 * A little scaffolding to avoid repeating over and over the same code (but allows for variants)
 * @author Andrea Aime (TOPP)
 *
 */
public class TestRunnerScaffolding {
    protected TestMetadata metadata;
    protected TestProperties emptyProperties;
    protected TestExecutor executor;
    protected TestExecutorFactory factory;
    protected TestListener[] listeners;
    protected TestRunner runner;
    
    
    protected TestRunner buildTestRunner() {
        return new ConformanceTestRunner();
    }

    public void performTest() throws Throwable {
        metadata = new TestMetadata("test", "whosGonnaTestTheTests", "0.1");
        emptyProperties = new TestPropertiesImpl();
        executor = buildExecutor();
        factory = buildFactory(executor);
        listeners = buildTestListeners(executor);
        runner = buildTestRunner();
        
        // run the tests
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