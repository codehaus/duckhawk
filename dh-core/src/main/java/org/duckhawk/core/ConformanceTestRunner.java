package org.duckhawk.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConformanceTestRunner implements TestRunner {
    /**
     * The logger for the {@link TestRunner} hierarchy
     */
    protected static Log LOGGER = LogFactory.getLog(TestRunner.class);

    /**
     * Flag is raised when cancelling occurrs
     */
    protected boolean cancelled;

    /**
     * The prototype executor (used directly if the test needs just one)
     */
    protected TestExecutor executor;

    /**
     * The test context, contains environment and listeners
     */
    protected TestContext context;

    /**
     * The metadata reported in test events
     */
    private TestMetadata metadata;

    /**
     * Prepares the test runner to run the {@link TestExecutor} object generated
     * by the factory
     */
    public ConformanceTestRunner(TestContext context, TestExecutor executor) {
        this.context = context;
        this.executor = executor;
    }

    public TestExecutor getTestExecutor() {
        return executor;
    }

    public TestContext getContext() {
        return context;
    }

    public void runTests() {
        // reset cancellation state and setup the test properties
        cancelled = false;
        TestProperties testProperties = new TestPropertiesImpl();
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.conformance
                .toString());
        fireStartEvent(testProperties, 1);
        try {
            TestProperties callProperties = new TestPropertiesImpl();
            prepareCallProperties(callProperties, 0);
            runSingle(executor, callProperties);
        } finally {
            // report back that we're done
            fireEndEvent(testProperties);
        }
    }

    /**
     * Clears the call properties and sets up the standard call properties
     * (thread id and call number)
     * 
     * @param properties
     * @param callIndex
     */
    protected void prepareCallProperties(TestProperties properties,
            int callIndex) {
        properties.clear();
        properties.put(TestExecutor.KEY_CALL_NUMBER, callIndex + 1);
        properties.put(TestExecutor.KEY_CALL_TIME, System.currentTimeMillis() - context.getStart());
    }

    /**
     * Returns the type of this test according to the {@link TestType}
     * classification
     * 
     * @return
     */
    public TestType getTestType() {
        return TestType.conformance;
    }

    /**
     * Returns the {@link TestMetadata} for this runner
     * 
     * @return
     */
    protected TestMetadata getMetadata() {
        if (metadata == null) {
            metadata = new TestMetadata(context.getProductId(), context
                    .getProductVersion(), executor.getTestId(), getTestType());
        }
        return metadata;
    }

    public void dispose() {
        // nothing to do
    }

    // protected void addTestRunListener(TestListener listener) {
    // listeners.add(listener);
    // }
    //
    // protected void removeTestRunListener(TestListener listener) {
    // listeners.remove(listener);
    // }

    public void cancel() {
        cancelled = true;
        try {
            // if we have an active executor, notify cancel to it
            if (executor != null)
                executor.cancel();
        } catch (Throwable t) {
            LOGGER
                    .warn("Exception occurred while cancelling the execution ",
                            t);
        }
    }

    /**
     * Executes a single call to the {@link TestExecutor} and handles notifies
     * listeners of the result
     * 
     * @param executor
     * @param metadata
     * @param runProperties
     * @return
     */
    protected double runSingle(TestExecutor executor,
            TestProperties runProperties) {
        // default values
        double time = 0d;
        long start = 0l;
        long end;

        // make sure we don't even start if this has been cancelled
        if (cancelled)
            return 0d;

        Throwable exception = null;
        // run the timed part and time it no matter what happens
        try {
            start = System.nanoTime();
            executor.run(runProperties);
        } catch (Throwable t) {
            exception = t;
        } finally {
            end = System.nanoTime();
        }

        // if not cancelled run the check part
        if (!cancelled && exception == null) {
            try {
                executor.check(runProperties);
            } catch (Throwable t) {
                exception = t;
            }
        }

        // compute time and fire events
        time = ((end - start) / 1000000000.0);
        fireCallEvent(time, executor, runProperties, exception);
        return time;
    }

    /**
     * Notifies listeners that a single call to the {@link TestExecutor} is
     * completed, and provides the results.
     * 
     * @param time
     * @param executor
     * @param throwable
     */
    protected void fireCallEvent(double time, TestExecutor executor,
            TestProperties properties, Throwable throwable) {
        List<TestListener> listeners = context.getListeners();
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            // This part is synchronized so that the listeners do not have to
            // deal with concurrency issues
            synchronized (listeners) {
                listener.testCallExecuted(executor, getMetadata(), properties,
                        time, throwable);
            }
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param testProperties
     * 
     * @param baseExecutor
     * @param callCount
     */
    protected void fireStartEvent(TestProperties testProperties, int callCount) {
        List<TestListener> listeners = context.getListeners();
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            synchronized (listener) {
                listener.testRunStarting(getMetadata(), testProperties,
                        callCount);
            }
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param testProperties
     * 
     * @param baseExecutor
     * @param callCount
     */
    protected synchronized void fireEndEvent(TestProperties testProperties) {
        List<TestListener> listeners = context.getListeners();
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            listener.testRunCompleted(metadata, testProperties);
        }
    }
}
