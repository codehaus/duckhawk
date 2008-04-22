package org.duckhawk.core;

public class StressTestRunner extends PerformanceTestRunner {

    /**
     * Number of concurrent threads running the TestExecutor
     */
    protected int numThreads;

    /**
     * Number of threads that still have to terminate
     */
    protected int expiredThreads;

    public StressTestRunner(int perThreadRepetitions, int numThreads) {
        super(perThreadRepetitions);
        this.numThreads = numThreads;
        ensurePositive(numThreads, "numThreads");

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

    /**
     * Runs the calls with parallel threads
     * 
     * @TODO: make it possible to reuse the stress test runner and use a thread
     *        pool and the java concurrency API to manage it
     * @param factory
     * @param metadata
     */
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

}
