package org.duckhawk.core;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IAnswer;



/**
 * A little scaffolding to avoid repeating over and over the same code (but allows for variants)
 * @author   Andrea Aime (TOPP)
 */
public class TestRunnerScaffolding {
    protected TestMetadata metadata;
    protected TestProperties emptyProperties;
    protected List<TestExecutor> executors = new ArrayList<TestExecutor>();
    protected TestExecutorFactory factory;
    /**
     * @uml.property  name="listeners"
     * @uml.associationEnd  multiplicity="(0 -1)"
     */
    protected TestListener[] listeners;
    protected TestRunner runner;
    protected int expectedTestExecutors;
    protected int expectedRunCount;
    
    public TestRunnerScaffolding() {
        this(1, 1);
    }
    
    public TestRunnerScaffolding(int expectedTestExecutors, int expectedRunCount) {
        this.expectedTestExecutors = expectedTestExecutors;
        this.expectedRunCount = expectedRunCount;
    }
    
    
    protected TestRunner buildTestRunner() {
        return new ConformanceTestRunner();
    }

    public void performTest() throws Throwable {
        init();
        run();
        check();
    }

    private void check() {
        // make sure all expectations are matched
        verify(factory);
        for (TestExecutor executor : executors) {
            verify(executor);
        }
        for (TestListener testListener : listeners) {
            verify(testListener);
        }
    }

    private void init() throws Throwable {
        metadata = new TestMetadata("test", "whosGonnaTestTheTests", "0.1");
        emptyProperties = new TestPropertiesImpl();
        factory = buildFactory(expectedTestExecutors, expectedRunCount);
        listeners = buildTestListeners();
        runner = buildTestRunner();
    }

    protected void run() {
        // run the tests
        for (TestListener testListener : listeners) {
            runner.addTestRunListener(testListener);
        }
        runner.runTests(factory);
        runner.dispose();
    }

    protected TestListener[] buildTestListeners() {
        // build a listener and set expectations
        TestListener listener = createMock(TestListener.class);
        listener.testRunStarting(metadata, emptyProperties, 1);
        listener.testCallExecuted(isA(TestExecutor.class), same(metadata),
                eq(emptyProperties), anyDouble(), eq((Throwable) null));
        listener.testRunCompleted(metadata, emptyProperties);
        replay(listener);
        return new TestListener[] {listener};
    }

    protected TestExecutorFactory buildFactory(int expectedTestExecutors, int expectedRunCount) throws Throwable {
        // build a factory and set expectations
        TestExecutorFactory factory = createMock(TestExecutorFactory.class);
        expect(factory.createMetadata()).andReturn(metadata).times(expectedRunCount);
        expect(factory.createTestExecutor()).andAnswer(new IAnswer<TestExecutor>() {
            
            public TestExecutor answer() throws Throwable {
                TestExecutor executor = buildExecutor();
                executors.add(executor);
                return executor;
            }
        
        }).times(expectedTestExecutors * expectedRunCount);
        replay(factory);
        return factory;
    }

    protected TestExecutor buildExecutor() throws Throwable {
        // build an executor that does nothing (and set expectations)
        TestExecutor executor = createMock(TestExecutor.class);
        executor.run(emptyProperties);
        executor.check(emptyProperties);
        replay(executor);
        return executor;
    }
}