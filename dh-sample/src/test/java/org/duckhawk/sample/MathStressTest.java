package org.duckhawk.sample;

import java.io.File;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.junit3.StressTest;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class MathStressTest extends StressTest {

    static TestListener[] listeners;

    private static TestListener[] getListeners() {
        if (listeners == null) {
            listeners = new TestListener[] {
                    new PerformanceSummarizer(), //
                    new PrintStreamListener(true, true), // 
                    new XStreamDumper(new File("./target/dh-report"))
            };
        }
        return listeners;
    }

    public MathStressTest() {
        // Load test, 5 threads in parallel each doing 10 requests, with a ramp
        // up time of 3 seconds
        super("Math", "1.0", 10, 5, 2, getListeners());
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
