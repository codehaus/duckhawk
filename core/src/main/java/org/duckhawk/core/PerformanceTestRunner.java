package org.duckhawk.core;

import java.security.InvalidParameterException;

public class PerformanceTestRunner extends ConformanceTestRunner implements
        TestRunner {
    /**
     * How many times the test must be run (besides the warm up run)
     */
    int repetitions;

    /**
     * Number of concurrent threads running the TestExecutor
     */
    protected int numThreads;

    /**
     * Number of threads that still have to terminate
     */
    private int expiredThreads;

    public PerformanceTestRunner(int repetitions) {
        this(repetitions, 1);
    }

    public PerformanceTestRunner(int repetitions, int numThreads) {
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

    public void runTests(TestExecutorFactory factory) {
        int callCount = numThreads * repetitions;
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        fireStartEvent(metadata, testProperties, callCount);
        try {
            if (numThreads == 1) {
                TestExecutor executor = factory.createTestExecutor();
                runRepeated(executor, metadata);
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
                        runRepeated(factory.createTestExecutor(), metadata);
                    } finally {
                        // no matter what happens mark this thread as "done"
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

    protected void runRepeated(TestExecutor executor, TestMetadata metadata) {
        // warmup
        TestProperties runProperties = new TestPropertiesImpl();
        try {
            executor.run(runProperties);
        } catch (Throwable t) {
            // notify
        }

        // loop and time
        for (int i = 0; i < repetitions; i++) {
            // clean up properties and run test
            runProperties.clear();
            runSingle(executor, metadata, runProperties);
        }
    }
}
