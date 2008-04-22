package org.duckhawk.junit3;

import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestRunner;

public class StressTest extends AbstractDuckHawkTest {

    protected int times;

    private TestListener[] listeners;

    private int numThreads;

    private int rampUp;

    public StressTest(String productId, String productVersion, int times,
            int numThreads, int rampUp, TestListener... listeners) {
        super(productId, productVersion);
        this.times = times;
        this.numThreads = numThreads;
        this.listeners = listeners;
        this.rampUp = rampUp;
    }
    
    protected TestRunner getTestRunner() {
        TestRunner runner = new StressTestRunner(times, numThreads, rampUp);
        for (TestListener listener : listeners) {
            runner.addTestRunListener(listener);
        }
        return runner;
    }

}
