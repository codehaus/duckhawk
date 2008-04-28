package org.duckhawk.core;

import java.util.ArrayList;
import java.util.List;

public class ConformanceTestRunner implements TestRunner {

    /**
     * The test listeners
     */
    protected List<TestListener> listeners = new ArrayList<TestListener>();

    public void runTests(TestExecutorFactory factory) {
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.conformance.toString());
        fireStartEvent(metadata, testProperties, 1);
        try {
            TestExecutor executor = factory.createTestExecutor();
            runSingle(executor, metadata, new TestPropertiesImpl());
        } finally {
            fireEndEvent(metadata, testProperties);
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
    protected double runSingle(TestExecutor executor, TestMetadata metadata,
            TestProperties runProperties) {
        // default values
        double time = 0d;
        long start = 0l;
        long end;
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
        // run the check part
        if(exception == null) {
            try {
                executor.check(runProperties);
            } catch (Throwable t) {
                exception = t;
            }
        } else {
            System.out.println("Exception " + exception);
        }
        time = ((end - start) / 1000000000.0);
        fireCallEvent(time, executor, metadata, runProperties, exception);
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
            TestMetadata metadata, TestProperties properties,
            Throwable throwable) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            // This part is synchronized so that the listeners do not have to
            // deal with concurrency issues
            synchronized (listeners) {
                listener.testCallExecuted(executor, metadata, properties, time,
                        throwable);
            }
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param testProperties
     * 
     * @param executor
     * @param callCount
     */
    protected synchronized void fireStartEvent(TestMetadata metadata,
            TestProperties testProperties, int callCount) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            listener.testRunStarting(metadata, testProperties, callCount);
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param testProperties
     * 
     * @param executor
     * @param callCount
     */
    protected synchronized void fireEndEvent(TestMetadata metadata,
            TestProperties testProperties) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TestListener listener : listeners) {
            listener.testRunCompleted(metadata, testProperties);
        }
    }

    public void addTestRunListener(TestListener listener) {
        listeners.add(listener);
    }

    public void removeTestRunListener(TestListener listener) {
        listeners.remove(listener);
    }

    public void dispose() {
        listeners.clear();
    }

}
