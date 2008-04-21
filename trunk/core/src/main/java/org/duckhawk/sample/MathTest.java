package org.duckhawk.sample;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TimedTestListener;
import org.duckhawk.core.util.PerformanceSummarizer;
import org.duckhawk.junit3.TimedTest;

public class MathTest extends TimedTest {

    public MathTest() {
        // Load test, 10 threads in parallel each doing 50 requests
        super("Math", "1.0", 50, 10, new PerformanceSummarizer(), new SysOutListener());
        // Perf test, just 50 repetitions
        // super("Math", "1.0", 50, 1, new SysOutListener());
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

    public static class SysOutListener implements TimedTestListener {
        NumberFormat format = new DecimalFormat("0.#######");

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, TestProperties properties, double time, Throwable exception) {
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": ";
            if (exception != null)
                msg += "FAILED,  " + exception.getMessage();
            else
                msg += format.format(time) + "s";
            msg += "\nProperties: " + properties;
            System.out.println(msg);
       }

        public void testRunCompleted(TestMetadata metadata, TestProperties properties) {
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": completed! Properties " + properties;
            System.out.println(msg);

        }

        public void testRunStarting(TestMetadata metadata, TestProperties properties, int callCount) {
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": started!";
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        TestRunner.run(MathTest.class);
    }

}
