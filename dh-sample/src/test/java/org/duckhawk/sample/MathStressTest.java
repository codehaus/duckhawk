package org.duckhawk.sample;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.StressTest;

public class MathStressTest extends StressTest {

    public MathStressTest() {
        // Load test, 5 threads in parallel each doing 10 requests, with a ramp
        // up time of 3 seconds
        super(TestSupport.getContext(), 10, 5, 2);
    }

    public void testSqrt() throws Exception {
        // make this test blow up 10% of the times
        if(Math.random() < 0.1)
            throw new Exception("This is a random failure in testSqrt");
        
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
