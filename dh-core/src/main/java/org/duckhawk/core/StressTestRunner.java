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
        long rampDelay = numThreads == 1 ? 0 : rampUp * 1000 / (numThreads - 1);
        long start = System.nanoTime();
        for (int i = 0; i < numThreads; i++) {
            // make sure we sleep only the time necessary to get to the next 
            // start time in the ramp (provided there is a ramp, of course)
            long targetStartTime = rampDelay * i;
            long sleepTime = targetStartTime - (System.nanoTime() - start) / 1000000;
            while(rampDelay > 0 && sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch(InterruptedException e) {
                    // forget about it and go back on sleeping if necessary
                }
                sleepTime = targetStartTime - (System.nanoTime() - start) / 1000000;
            }
            
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
