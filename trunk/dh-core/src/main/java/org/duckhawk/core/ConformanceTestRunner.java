package org.duckhawk.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConformanceTestRunner implements TestRunner {
    /**
     * The logger for the {@link TestRunner} hierarchy
     */
    protected static Log LOGGER = LogFactory.getLog(TestRunner.class);

    /**
     * The test listeners
     */
    protected List<TestListener> listeners = new ArrayList<TestListener>();

    /**
     * Flag is raised when cancelling occurrs
     */
    protected boolean cancelled;

    /**
     * The current executor (provided the test needs just one)
     */
    protected TestExecutor executor;

    public void runTests(TestExecutorFactory factory) {
        cancelled = false;
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.conformance
                .toString());
        fireStartEvent(metadata, testProperties, 1);
        try {
            executor = factory.createTestExecutor();
            runSingle(executor, metadata, new TestPropertiesImpl());
        } finally {
            // free up the executor and report back that we're done
            executor = null;
            fireEndEvent(metadata, testProperties);
        }
    }

    public void dispose() {
        listeners.clear();
    }

    public void addTestRunListener(TestListener listener) {
        listeners.add(listener);
    }

    public void removeTestRunListener(TestListener listener) {
        listeners.remove(listener);
    }
    
    public void cancel() {
        cancelled = true;
        try {
            // if we have an active executor, notify cancel to it
            if(executor != null)
                executor.cancel();
        } catch(Throwable t) {
            LOGGER.warn("Exception occurred while cancelling the execution ", t);
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
        
        // make sure we don't even start if this has been cancelled
        if(cancelled)
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
        if(!cancelled && exception == null) {
            try {
                executor.check(runProperties);
            } catch (Throwable t) {
                exception = t;
            }
        }
        
        // compute time and fire events
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
}
