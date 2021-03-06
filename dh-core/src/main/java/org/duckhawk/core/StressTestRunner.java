/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.duckhawk.core;

import java.util.ArrayList;
import java.util.Collections;
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
    protected List<LinearRunThread> runningThreads = Collections.synchronizedList(new ArrayList<LinearRunThread>());

    /**
     * Number of seconds
     */
    protected double rampUp;

    /**
     * A thread that runs the linear set of requests and allows access to its
     * {@link TestExecutor} instance
     */
    private class LinearRunThread extends Thread {
        TestExecutor executor;

        public LinearRunThread(TestExecutor executor) {
            this.executor = executor;
        }

        public void run() {
            try {
                // perform the run only if not cancelled
                if (!cancelled) {
                    runLinear(executor);
                }
            } finally {
                executor = null;
                // no matter what happens mark this thread as "done"
                testEnded(this);
            }
        }

    };

    /**
     * Creates a new stress tester that will use multiple threads to hit the
     * TestExecutors
     * 
     * @param perThreadRepetitions
     * @param numThreads
     * @param rampUp
     */
    public StressTestRunner(TestContext context, TestExecutor executor,
            int perThreadRepetitions, int numThreads, double rampUp) {
        super(context, executor, perThreadRepetitions);
        this.numThreads = numThreads;
        this.rampUp = rampUp;
        ensurePositive(numThreads, "numThreads", true);
        ensurePositive(rampUp, "rampUp", false);
    }

    /**
     * Creates a new stress tester that will use multiple threads to hit the
     * TestExecutors
     * 
     * @param perThreadRepetitions
     * @param numThreads
     */
    public StressTestRunner(TestContext context, TestExecutor executor,
            int perThreadRepetitions, int numThreads) {
        this(context, executor, perThreadRepetitions, numThreads, 0);
    }

    /**
     * Returns the type of this test according to the {@link TestType}
     * classification
     * 
     * @return
     */
    public TestType getTestType() {
        return TestType.stress;
    }

    @Override
    public void runTests() {
        // fill in the test wide properties with the configuration params
        TestProperties testProperties = new TestPropertiesImpl();
        executor.init(context.getEnvironment(), testProperties);
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.stress
                .toString());
        testProperties.put(KEY_REPETITIONS, repetitions);
        testProperties.put(KEY_NUMTHREADS, numThreads);
        testProperties.put(KEY_RAMPUP, rampUp);
        if (random != null) {
            testProperties.put(KEY_TIME, time);
            testProperties.put(KEY_DISTRIBUTION, random);
        }

        // launch the actual test
        runningThreads.clear();
        int callCount = numThreads * repetitions;
        fireStartEvent(testProperties, callCount);
        try {
            if (numThreads == 1) {
                runLinear(executor);
            } else {
                runParallel();
            }
        } finally {
            fireEndEvent(testProperties);
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
    protected void runParallel() {
        double rampDelay = numThreads == 1 ? 0 : rampUp * 1000
                / (numThreads - 1);
        long start = System.nanoTime();
        for (int i = 0; i < numThreads; i++) {
            // don't even try to start the thread if we have been cancelled
            if (cancelled)
                break;

            // make sure we sleep only the time necessary to get to the next
            // start time in the ramp (provided there is a ramp, of course)
            long targetStartTime = Math.round(rampDelay * i);
            if (rampDelay > 0) {
                sleepUpToTarget(start, targetStartTime);
            }

            // if cancel happens, we don't want to start a thread in parallel
            synchronized (this) {
                // we may have woken up due to cancellation, check
                if (cancelled)
                    break;

                // ok, start the thread
                LinearRunThread thread = new LinearRunThread(executor
                        .cloneExecutor());
                runningThreads.add(thread);
                thread.start();
            }
        }

        // if cancel happens, we don't want to go back to sleep in parallel
        synchronized (this) {
            while (runningThreads.size() > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // we want to wait for the threads no matter what
                }
            }
        }
    }
    
    @Override
    protected void prepareCallProperties(TestProperties properties,
            int callIndex) {
        super.prepareCallProperties(properties, callIndex);
        properties.put(TestExecutor.KEY_THREAD_ID, Thread.currentThread().getName());
    }

    private synchronized void testEnded(LinearRunThread thread) {
        runningThreads.remove(thread);
        notifyAll();
    }

    /**
     * The {@link ConformanceTestRunner} stop code won't cut it, we have to
     * inform all the {@link TestExecutor} instances used in this runner to
     * actually stop execution
     */
    public synchronized void cancel() {
        cancelled = true;
        try {
            // if we have an active executor, time to cancel them
            // (use a copy list to avoid locking the running threads list for too long)
            List<LinearRunThread> currentList = new ArrayList<LinearRunThread>(runningThreads);
            for (LinearRunThread thread : currentList) {
                try {
                    if (thread.executor != null)
                        thread.executor.cancel();
                    // thread might be sleeping on a wait(), wake it up
                    thread.interrupt();
                } catch (Exception e) {
                    LOGGER.warn("Exception occurred trying to "
                            + "cancel an execution thread ", e);
                }
            }

            // make sure we wake up the current thread, if it's sleeping
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            LOGGER.warn("Exception occurred while "
                    + "cancelling the execution ", t);
        }
    }

}
