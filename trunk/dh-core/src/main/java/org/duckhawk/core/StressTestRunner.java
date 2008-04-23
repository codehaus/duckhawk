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
    
    /**
     * Number of seconds
     */
    protected int rampUp;

    /**
     * Creates a new stress tester that will use multiple threads to hit the TestExecutors
     * @param perThreadRepetitions
     * @param numThreads
     * @param rampUp
     */
    public StressTestRunner(int perThreadRepetitions, int numThreads, int rampUp) {
        super(perThreadRepetitions);
        this.numThreads = numThreads;
        this.rampUp = rampUp;
        ensurePositive(numThreads, "numThreads", true);
        ensurePositive(rampUp, "rampUp", false);
    }
    
    public StressTestRunner(int perThreadRepetitions, int numThreads) {
        this(perThreadRepetitions, numThreads, 0);
    }

    public void runTests(TestExecutorFactory factory) {
        int callCount = numThreads * repetitions;
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        fireStartEvent(metadata, testProperties, callCount);
        try {
            if (numThreads == 1) {
                TestExecutor executor = factory.createTestExecutor();
                runLinear(executor, metadata);
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
        long rampDelay = numThreads == 1 ? 0 : rampUp * 1000 / (numThreads - 1);
        long start = System.nanoTime();
        for (int i = 0; i < numThreads; i++) {
            // make sure we sleep only the time necessary to get to the next 
            // start time in the ramp (provided there is a ramp, of course)
            long targetStartTime = rampDelay * i;
            if(rampDelay > 0) {
                sleepUpToTarget(start, targetStartTime);
            }
            
            Thread runner = new Thread() {

                public void run() {
                    try {
                        runLinear(factory.createTestExecutor(), metadata);
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
