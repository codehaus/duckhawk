package org.duckhawk.junit3;

import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestRunner;

/**
 * @author  Andrea Aime (TOPP)
 */
public abstract class StressTest extends AbstractDuckHawkTest {

    protected int times;

    /**
     * @uml.property  name="listeners"
     * @uml.associationEnd  multiplicity="(0 -1)"
     */
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
