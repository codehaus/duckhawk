package org.duckhawk.core;

import java.util.Random;

import junit.framework.TestCase;

public class StopRunnerTest extends TestCase {

    private StoppableTestExecutorFactory factory;
    private boolean testCompleted;
    private boolean testStarted;

    @Override
    protected void setUp() throws Exception {
        factory = new StoppableTestExecutorFactory();
    }

    private class TestRunnerLauncher implements Runnable {
        TestRunner runner;

        public TestRunnerLauncher(TestRunner runner) {
            this.runner = runner;
        }

        public void run() {
            runner.runTests(factory);
        }
    }
    
    private class StartEndTestListener implements TestListener {

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, TestProperties callProperties,
                double time, Throwable exception) {
            // do not care            
        }

        public void testRunCompleted(TestMetadata metadata,
                TestProperties testProperties) {
            testCompleted = true;
            
        }

        public void testRunStarting(TestMetadata metadata,
                TestProperties testProperties, int callNumber) {
            testStarted = true;
            
        }
        
    }

    public void testStopConformanceTest() throws Throwable {
        TestRunner runner = new ConformanceTestRunner();
        checkRunnerCanceling(runner, 1);
    }
    
    public void testStopPerformanceTest() throws Throwable {
        TestRunner runner = new PerformanceTestRunner(10);
        checkRunnerCanceling(runner, 1);
    }
    
    public void testStopDistributedPerformanceTest() throws Throwable {
        // let's try to use a very long distribution time, like one hour, and see if it actually stops
        TestRunner runner = new PerformanceTestRunner(10, 3600, new Random());
        checkRunnerCanceling(runner, 1);
    }
    
    public void testStopStressTest() throws Throwable {
        // let's try to use a very long distribution time, like one hour, and see if it actually stops
        TestRunner runner = new StressTestRunner(10, 10);
        checkRunnerCanceling(runner, 10);
    }
    
    public void testStopStressTestRampUp() throws Throwable {
        // let's try to use a very long distribution time, like one hour, and see if it actually stops
        TestRunner runner = new StressTestRunner(10, 10, 3600.0);
        checkRunnerCanceling(runner, -1);
    }

    /**
     * Checks that the provided runner actually supports cancelling the intended way
     * @param runner
     * @throws InterruptedException
     */
    void checkRunnerCanceling(TestRunner runner, int expectedExecutors)
            throws InterruptedException {
        runner.addTestRunListener(new StartEndTestListener());
        Thread t = new Thread(new TestRunnerLauncher(runner));
        t.start();

        // give it the time to start
        // (up to 20 seconds, should not take even a small fraction 
        // of that thought)
        for (int i = 0; i < 200; i++) {
            if(testStarted)
                break;
            
            Thread.sleep(200);
        }

        // cancel the execution
        runner.cancel();

        // stop and let it another bit for stopping down
        // (up to 20 seconds, should not take even a small fraction 
        // of that thought)
        for (int i = 0; i < 200; i++) {
            if(!t.isAlive())
                break;
            try {
                Thread.sleep(100);
            } catch(InterruptedException ie) {
            }
        }

        // now check everything went by the plans, that is, everything stopped
        assertFalse(t.isAlive());
        assertTrue(testCompleted);
        if(expectedExecutors > 0)
            assertEquals(expectedExecutors, factory.executors.size());
        for (StoppableTestExecutor executor : factory.executors) {
            assertTrue(executor.canceled);
            assertFalse(executor.timedOut);
            assertFalse(executor.checkPerformed);
        }
    }
}
