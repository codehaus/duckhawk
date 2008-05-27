package org.duckhawk.junit3;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestRunner;

/**
 * @author Andrea Aime (TOPP)
 */
public abstract class StressTest extends AbstractDuckHawkTest {

    protected int times;

    private int numThreads;

    private int rampUp;

    public StressTest(TestContext context, int times, int numThreads, int rampUp) {
        super(context);
        this.times = times;
        this.numThreads = numThreads;
        this.rampUp = rampUp;
    }

    protected TestRunner getTestRunner(TestContext context) {
        return new StressTestRunner(context, buildTestExecutor(), times,
                numThreads, rampUp);
    }

}
