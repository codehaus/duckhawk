package org.duckhawk.core;

import java.util.ArrayList;
import java.util.List;

public class StressTestRunner extends PerformanceTestRunner {
    
    /**
     * The number of threads used to run the stress test
     */
    public static final String KEY_NUMTHREADS = "stress.threadNumber";
    
    /**
     * The ramp up time in seconds, if any
     */
    public static final String KEY_RAMPUP = "stress.rampUpTime";

    /**
     * Number of concurrent threads running the TestExecutor
     */
    protected int numThreads;

    /**
     * Number of threads that still have to terminate
     */
    protected List<LinearRunThread> runningThreads = new ArrayList<LinearRunThread>();
    
    /**
     * Number of seconds
     */
    protected double rampUp;
    
    /**
     * A thread that runs the linear set of requests and allows access to its
     * {@link TestExecutor} instance
     */
    private class LinearRunThread extends Thread {
        TestExecutorFactory factory;
        TestExecutor executor;
        TestMetadata metadata;

        public LinearRunThread(TestExecutorFactory factory, TestMetadata metadata) {
            this.factory = factory;
            this.metadata = metadata;
        }
        
        public void run() {
            try {
                // perform the run only if not cancelled
                if(!cancelled) {
                    executor = factory.createTestExecutor();
                    runLinear(executor, metadata);
                }
            } finally {
                executor = null;
                // no matter what happens mark this thread as "done"
                testEnded(this);
            }
        }

    };
    

    /**
     * Creates a new stress tester that will use multiple threads to hit the TestExecutors
     * @param perThreadRepetitions
     * @param numThreads
     * @param rampUp
     */
    public StressTestRunner(int perThreadRepetitions, int numThreads, double rampUp) {
        super(perThreadRepetitions);
        this.numThreads = numThreads;
        this.rampUp = rampUp;
        ensurePositive(numThreads, "numThreads", true);
        ensurePositive(rampUp, "rampUp", false);
    }
    
    /**
     * Creates a new stress tester that will use multiple threads to hit the TestExecutors
     * @param perThreadRepetitions
     * @param numThreads
     */
    public StressTestRunner(int perThreadRepetitions, int numThreads) {
        this(perThreadRepetitions, numThreads, 0);
    }

    public void runTests(TestExecutorFactory factory) {
        // fill in the test wide properties with the configuration params 
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.stress.toString());
        testProperties.put(KEY_REPETITIONS, TestType.performance.toString());
        testProperties.put(KEY_NUMTHREADS, numThreads);
        testProperties.put(KEY_RAMPUP, rampUp);
        if(random != null) {
            testProperties.put(KEY_TIME, time);
            testProperties.put(KEY_DISTRIBUTION, random);
        }
        
        // launch the actual test
        runningThreads.clear();
        int callCount = numThreads * repetitions;
        fireStartEvent(metadata, testProperties, callCount);
        try {
            if (numThreads == 1) {
                executor = factory.createTestExecutor();
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
        double rampDelay = numThreads == 1 ? 0 : rampUp * 1000 / (numThreads - 1);
        long start = System.nanoTime();
        for (int i = 0; i < numThreads; i++) {
            // don't even try to start the thread if we have been cancelled
            if(cancelled)
                break;
            
            // make sure we sleep only the time necessary to get to the next 
            // start time in the ramp (provided there is a ramp, of course)
            long targetStartTime = Math.round(rampDelay * i);
            if(rampDelay > 0) {
                sleepUpToTarget(start, targetStartTime);
            }
            
            // we may have woken up due to cancellation, check
            if(cancelled)
                break;
            
            // ok, start the thread
            LinearRunThread thread = new LinearRunThread(factory, metadata);
            runningThreads.add(thread);
            thread.start();
        }

        while (runningThreads.size() > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                // we want to wait for the threads no matter what
            }
        }
    }

    private synchronized void testEnded(LinearRunThread thread) {
        runningThreads.remove(thread);
        notifyAll();
    }
    
    /**
     * The {@link ConformanceTestRunner} stop code won't cut it, we have to inform
     * all the {@link TestExecutor} instances used in this runner to actually stop 
     * execution
     */
    public void cancel() {
        cancelled = true;
        try {
            // if we have an active executor, time to 
            for (LinearRunThread thread : runningThreads) {
                try {
                    if(thread.executor != null)
                        thread.executor.cancel();
                    // thread might be sleeping on a wait(), wake it up
                    thread.interrupt();
                } catch(Exception e) {
                    LOGGER.warn("Exception occurred trying to cancel an execution thread ", e);
                }
            }
            
            // make sure we wake up the current thread, if it's sleeping
            Thread.currentThread().interrupt();
        } catch(Throwable t) {
            LOGGER.warn("Exception occurred while cancelling the execution ", t);
        } 
    }

}
