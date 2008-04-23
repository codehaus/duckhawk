package org.duckhawk.sample;


import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.PerformanceTest;
import org.duckhawk.util.PrintStreamListener;

public class MathPerfTest extends PerformanceTest {
    
    double result;
    
    @Override
    protected void setUp() throws Exception {
//        // TODO Auto-generated method stub
//        super.setUp();
    }

    public MathPerfTest() {
        super("Math", "1.0", 50, new PrintStreamListener(false, true));
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

}
