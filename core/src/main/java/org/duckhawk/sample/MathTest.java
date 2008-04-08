package org.duckhawk.sample;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TimedTestEvent;
import org.duckhawk.core.TimedTestListener;
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

        public void testRunExecuted(TimedTestEvent event) {
            TestExecutor executor = event.getSource();
            String msg = executor.getProductId() + " "
                    + executor.getProductVersion() + " - "
                    + executor.getTestId() + ": ";
            if (event.getException() != null)
                msg += "FAILED,  " + event.getException().getMessage();
            else
                msg += format.format(event.getTime()) + "s";
            System.out.println(msg);
        }

    }

    public static void main(String[] args) {
        TestRunner.run(MathTest.class);
    }

}
