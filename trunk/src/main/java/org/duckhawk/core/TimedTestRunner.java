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
        if (numThreads == 1) {
            runSingle(factory.buildTestExecutor());
        } else {
            runParallel(factory);
        }

    }

    protected synchronized void runParallel(final TestExecutorFactory factory) {
        expiredThreads = 0;
        for (int i = 0; i < numThreads; i++) {
            Thread runner = new Thread() {

                public void run() {
                    try {
                        runSingle(factory.buildTestExecutor());
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

    protected void runSingle(TestExecutor executor) {
        // core loop: warmup, repeat and measure, catch exceptions
        final long start;
        final long end;
        try {
            // warmup
            executor.run();

            // loop and time
            start = System.nanoTime();
            for (int i = 0; i < repetitions; i++)
                executor.run();
            end = System.nanoTime();
        } catch (Throwable t) {
            // notify
            fireTimeEvent(0d, executor, t);
            return;
        }

        // compute average time in seconds
        final double time = ((end - start) / 1000000000.0) / repetitions;
        fireTimeEvent(time, executor, null);
    }

    private void fireTimeEvent(double time, TestExecutor executor,
            Throwable throwable) {
        if (listeners.size() == 0)
            return;

        // build the event object
        TimedTestEvent event = null;
        if (throwable != null)
            event = new TimedTestEvent(executor, time, throwable);
        else
            event = new TimedTestEvent(executor, time);

        // notify listeners
        for (TimedTestListener listener : listeners) {
            listener.testRunExecuted(event);
        }
    }

    public void addTestRunListener(TimedTestListener listener) {
        listeners.add(listener);
    }

    public void removeTestRunListener(TimedTestListener listener) {
        listeners.remove(listener);
    }

}
