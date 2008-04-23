package org.duckhawk.sample;


import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.PerformanceTest;
import org.duckhawk.util.PrintStreamListener;

public class MathPerfTest extends PerformanceTest {
    
    @Override
    protected void setUp() throws Exception {
//        // TODO Auto-generated method stub
//        super.setUp();
    }

    public MathPerfTest() {
        super("Math", "1.0", 50, new PrintStreamListener(true, true));
    }

    public void testSqrt() {
        properties.put(TestExecutor.KEY_REQUEST, "sqrt(12.5)");
        double result = Math.sqrt(12.5);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testSin() {
        properties.put(TestExecutor.KEY_REQUEST, "sin(12.5)");
        double result = Math.sin(12.5);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testLog() {
        properties.put(TestExecutor.KEY_REQUEST, "log(12.5)");
        double result = Math.log(12.5);
        properties.put(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public static void main(String[] args) {
        TestRunner.run(MathPerfTest.class);
    }

}
