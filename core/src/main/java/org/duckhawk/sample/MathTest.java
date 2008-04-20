package org.duckhawk.sample;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TimedTestListener;
import org.duckhawk.core.util.PerformanceSummarizer;
import org.duckhawk.junit3.TimedTest;

public class MathTest extends TimedTest {

    public MathTest() {
        // Load test, 10 threads in parallel each doing 50 requests
        super("Math", "1.0", 50, 10, new SysOutListener());
        // Perf test, just 50 repetitions
        // super("Math", "1.0", 50, 1, new SysOutListener());
    }

    public void testSqrt() {
        Math.sqrt(12.5);
    }

    public void testSin() {
        Math.sin(12.5);
    }

    public void testLog() {
        Math.log(12.5);
    }

    public static class SysOutListener implements TimedTestListener {
        NumberFormat format = new DecimalFormat("0.#######");

        PerformanceSummarizer summarizer = new PerformanceSummarizer();

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, double time, Throwable exception) {
            summarizer.accumulate(time);
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": ";
            if (exception != null)
                msg += "FAILED,  " + exception.getMessage();
            else
                msg += format.format(time) + "s";
            System.out.println(msg);

        }

        public void testRunCompleted(TestMetadata metadata) {
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": completed!";
            System.out.println(msg);
            summarizer.done();
            System.out.println(summarizer);

        }

        public void testRunStarting(TestMetadata metadata, int callCount) {
            String msg = metadata.getProductId() + " "
                    + metadata.getProductVersion() + " - "
                    + metadata.getTestId() + ": started!";
            System.out.println(msg);
            summarizer.start(callCount);
        }

    }

    public static void main(String[] args) {
        TestRunner.run(MathTest.class);
    }

}
