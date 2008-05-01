package org.duckhawk.core;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A little scaffolding to avoid repeating over and over the same code (but
 * allows for variants)
 * 
 * @author Andrea Aime (TOPP)
 */
public class TestRunnerScaffolding {
    protected TestMetadata metadata;

    protected TestProperties emptyProperties;

    protected TestExecutor baseExecutor;

    protected List<TestExecutor> cloneExecutors = new ArrayList<TestExecutor>();

    /**
     * @uml.property name="listeners"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    protected TestListener[] listeners;

    protected TestRunner runner;

    protected int expectedTestExecutors;

    protected int expectedRunCount;

    private TestContext context;

    private TestType type;

    public TestRunnerScaffolding(TestType type) {
        this(type, 1, 1);
    }

    public TestRunnerScaffolding(TestType type, int expectedTestExecutors,
            int expectedRunCount) {
        this.type = type;
        this.expectedTestExecutors = expectedTestExecutors;
        this.expectedRunCount = expectedRunCount;
    }

    protected TestRunner buildTestRunner(TestContext context,
            TestExecutor executor) {
        return new ConformanceTestRunner(context, executor);
    }

    public void performTest() throws Throwable {
        init();
        run();
        check();
    }

    private void check() {
        // make sure all expectations are matched
        verify(baseExecutor);
        for (TestExecutor executor : cloneExecutors) {
            verify(executor);
        }
        for (TestListener testListener : listeners) {
            verify(testListener);
        }
    }

    void init() throws Throwable {
        metadata = new TestMetadata("whosGonnaTestTheTests", "0.1", "test",
                type);
        emptyProperties = new TestPropertiesImpl();
        listeners = buildTestListeners();
        context = new TestContext("whosGonnaTestTheTests", "0.1", emptyProperties, listeners);
        baseExecutor = buildExecutor();

        runner = buildTestRunner(context, baseExecutor);
    }

    protected void run() {
        runner.runTests();
        runner.dispose();
    }

    protected TestListener[] buildTestListeners() {
        // build a listener and set expectations
        TestListener listener = createMock(TestListener.class);
        listener
                .testRunStarting(eq(metadata), isA(TestProperties.class), eq(1));
        listener.testCallExecuted(isA(TestExecutor.class), eq(metadata),
                eq(emptyProperties), anyDouble(), eq((Throwable) null));
        listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
        replay(listener);
        return new TestListener[] { listener };
    }

    // protected TestExecutorFactory buildFactory(int expectedTestExecutors, int
    // expectedRunCount) throws Throwable {
    // // build a factory and set expectations
    // TestExecutorFactory factory = createMock(TestExecutorFactory.class);
    // expect(factory.createMetadata()).andReturn(metadata).times(expectedRunCount);
    // expect(factory.createTestExecutor()).andAnswer(new
    // IAnswer<TestExecutor>() {
    //            
    // public TestExecutor answer() throws Throwable {
    // TestExecutor executor = buildExecutor();
    // cloneExecutors.add(executor);
    // return executor;
    // }
    //        
    // }).times(expectedTestExecutors * expectedRunCount);
    // replay(factory);
    // return factory;
    // }

    protected TestExecutor buildExecutor() throws Throwable {
        // build an executor that does nothing (and set expectations)
        TestExecutor executor = createMock(TestExecutor.class);
        executor.run(emptyProperties);
        executor.check(emptyProperties);
        expect(executor.getTestId()).andReturn("test").anyTimes();
        replay(executor);
        return executor;
    }
}