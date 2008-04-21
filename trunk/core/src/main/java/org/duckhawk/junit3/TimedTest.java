package org.duckhawk.junit3;

import org.duckhawk.core.TimedTestListener;
import org.duckhawk.core.TimedTestRunner;

public abstract class TimedTest extends AbstractDuckHawkTest {

    protected int times;
    private TimedTestListener[] listeners;
    private int numThreads;
    

    public TimedTest(String productId, String productVersion, int times, int numThreads, TimedTestListener... listeners) {
        super(productId, productVersion);
        this.times = times;
        this.numThreads = numThreads;
        this.listeners = listeners;
    }

    protected TimedTestRunner getTestRunner() {
        TimedTestRunner runner = new TimedTestRunner(times, numThreads);
        for (TimedTestListener listener : listeners) {
            runner.addTestRunListener(listener);
        }
        return runner;
    }
}
