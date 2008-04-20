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
        TestMetadata metadata = factory.getMetadata();
        fireStartEvent(metadata, callCount);
        try {
            if (numThreads == 1) {
                TestExecutor executor = factory.buildTestExecutor();
                runSingle(executor, metadata);
            } else {
                runParallel(factory, metadata);
            }
        } finally {
            fireEndEvent(metadata);
        }

    }

    protected synchronized void runParallel(final TestExecutorFactory factory,
            final TestMetadata metadata) {
        expiredThreads = 0;
        for (int i = 0; i < numThreads; i++) {
            Thread runner = new Thread() {

                public void run() {
                    try {
                        runSingle(factory.buildTestExecutor(), metadata);
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
        try {
            executor.run();
        } catch (Throwable t) {
            // notify
        }

        // loop and time
        for (int i = 0; i < repetitions; i++) {
            try {
                long start = System.nanoTime();
                executor.run();
                long end = System.nanoTime();
                double time = ((end - start) / 1000000000.0);
                totalTime += time;
                fireTimeEvent(time, executor, metadata, null);
            } catch (Throwable t) {
                // notify
                fireTimeEvent(0d, executor, metadata, t);
            }
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
            TestMetadata metadata, Throwable throwable) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
            // This part is synchronized so that the listeners do not have to
            // deal with concurrency issues
            synchronized (this) {
                listener.testCallExecuted(executor, metadata, time, throwable);
            }
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param executor
     * @param callCount
     */
    private synchronized void fireStartEvent(TestMetadata metadata,
            int callCount) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
            listener.testRunStarting(metadata, callCount);
        }
    }

    /**
     * Notifies listeners that a performance test is about to start
     * 
     * @param executor
     * @param callCount
     */
    private synchronized void fireEndEvent(TestMetadata metadata) {
        if (listeners.size() == 0)
            return;

        // notify listeners
        for (TimedTestListener listener : listeners) {
            listener.testRunCompleted(metadata);
        }
    }

    public void addTestRunListener(TimedTestListener listener) {
        listeners.add(listener);
    }

    public void removeTestRunListener(TimedTestListener listener) {
        listeners.remove(listener);
    }

}
