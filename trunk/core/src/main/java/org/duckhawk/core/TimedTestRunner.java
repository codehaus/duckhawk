package org.duckhawk.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class TimedTestRunner {
    List<TimedTestListener> listeners = new ArrayList<TimedTestListener>();

    /**
     * How many times the test must be run (besides the warm up run)
     */
    int repetitions;

    /**
     * Number of concurrent threads running the TestExecutor
     */
    protected int numThreads;

    private int expiredThreads;

    public TimedTestRunner(int repetitions) {
        this(repetitions, 1);
    }

    public TimedTestRunner(int repetitions, int numThreads) {
        this.repetitions = repetitions;
        this.numThreads = numThreads;
        ensurePositive(repetitions, "repetitions");
        ensurePositive(numThreads, "numThreads");

    }

    private void ensurePositive(int number, String variable) {
        if (number <= 0)
            throw new InvalidParameterException("Parameter " + variable
                    + " must be positive");
    }

    public void evaluatePerformance(TestExecutorFactory factory) {
        int callCount = numThreads * repetitions;
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        fireStartEvent(metadata, testProperties, callCount);
        try {
            if (numThreads == 1) {
                TestExecutor executor = factory.createTestExecutor();
                runSingle(executor, metadata);
            } else {
                runParallel(factory, metadata);
            }
        } finally {
            fireEndEvent(metadata, testProperties);
        }

    }

    protected synchronized void runParallel(final TestExecutorFactory factory,
            final TestMetadata metadata) {
        expiredThreads = 0;
        for (int i = 0; i < numThreads; i++) {
            Thread runner = new Thread() {

                public void run() {
                    try {
                        runSingle(factory.createTestExecutor(), metadata);
                    } finally {
                        // no matter what happens notify the test run ended
                        testEnded();
                    }
                }

            };
            runner.start();
        }

        while (expiredThreads < numThreads) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private synchronized void testEnded() {
        expiredThreads++;
        notifyAll();
    }

    protected double runSingle(TestExecutor executor, TestMetadata metadata) {
        // core loop: warmup, repeat and measure, catch exceptions
        double totalTime = 0;

        // warmup
        TestProperties runProperties = new TestPropertiesImpl();
        try {
            executor.run(runProperties);
        } catch (Throwable t) {
            // notify
        }

        // loop and time
        for (int i = 0; i < repetitions; i++) {
            // default values and properties cleanup
            double time = 0d;
            Throwable exception = null;
            runProperties.clear();
            try {
                long start = System.nanoTime();
                executor.run(runProperties);
                long end = System.nanoTime();
                time = ((end - start) / 1000000000.0);
                totalTime += time;
            } catch (Throwable t) {
                exception = t;
            }
            fireTimeEvent(time, executor, metadata, runProperties, exception);
        }

        return totalTime;
    }

    /**
     * Notifies listeners that a single call to the {@link TestExecutor} is
     * completed, and provides the results.
     * 
     * @param time
     * @param executor
     * @param throwable
     */
    private void fireTimeEvent(double time, TestExecutor executor,
            TestMetadata metadata, TestProperties properties,
            Throwable throwable) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
            // This part is synchronized so that the listeners do not have to
            // deal with concurrency issues
            synchronized (this) {
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
    private synchronized void fireStartEvent(TestMetadata metadata,
            TestProperties testProperties, int callCount) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
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
    private synchronized void fireEndEvent(TestMetadata metadata,
            TestProperties testProperties) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
            listener.testRunCompleted(metadata, testProperties);
        }
    }

    public void addTestRunListener(TimedTestListener listener) {
        listeners.add(listener);
    }

    public void removeTestRunListener(TimedTestListener listener) {
        listeners.remove(listener);
    }

}
