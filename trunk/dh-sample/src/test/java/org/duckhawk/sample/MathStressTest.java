package org.duckhawk.sample;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.StressTest;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class MathStressTest extends StressTest {

    public MathStressTest() {
        // Load test, 5 threads in parallel each doing 50 requests, with a ramp up time of 3 seconds
        super("Math", "1.0", 50, 5, 3, new PerformanceSummarizer(), new PrintStreamListener(true, true));
    }

    public void testSqrt() throws Exception {
        properties.put(TestExecutor.KEY_REQUEST, "sqrt(12.5)");
        double result = Math.sqrt(12.5);
        Thread.sleep(50);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testSin() throws Exception {
        properties.put(TestExecutor.KEY_REQUEST, "sin(12.5)");
        double result = Math.sin(12.5);
        Thread.sleep(50);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testLog() throws Exception {
        properties.put(TestExecutor.KEY_REQUEST, "log(12.5)");
        double result = Math.log(12.5);
        Thread.sleep(50);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
}
