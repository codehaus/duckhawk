package org.duckhawk.sample;


import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.PerformanceTest;

public class MathPerfTest extends PerformanceTest {
    
    double result;
    
    @Override
    protected void setUp() throws Exception {
        // Keep this empty method here, a bug occurred that was triggered when setUp() did not
        // call back the super one
    }

    public MathPerfTest() {
        super("Math", "1.0", 50, TestSupport.getListeners());
    }

    public void testSqrt() {
        properties.put(TestExecutor.KEY_REQUEST, "sqrt(12.5)");
        result = Math.sqrt(4);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
    
    public void checkSqrt() {
        // make an assertion that will fail
        assertEquals(4.0, result);
    }

    public void testSin() {
        properties.put(TestExecutor.KEY_REQUEST, "sin(12.5)");
        result = Math.sin(12.5);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testLog() {
        properties.put(TestExecutor.KEY_REQUEST, "log(12.5)");
        result = Math.log(12.5);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
    
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        runner.run(MathPerfTest.class);
    }

}
