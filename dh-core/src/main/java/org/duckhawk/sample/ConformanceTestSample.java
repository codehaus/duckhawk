package org.duckhawk.sample;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.ConformanceTest;

public class ConformanceTestSample extends ConformanceTest {

    public ConformanceTestSample() {
        super("RandomConformance", "1.0", new SysOutListener());
    }

    public void testSumFloats() {
        float sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += 0.01f;
        }
        assertEquals(2.0f, sum);
    }


    public static class SysOutListener implements TestListener {
        NumberFormat format = new DecimalFormat("0.#######");

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, TestProperties properties, double time,
                Throwable exception) {
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

        public void testRunCompleted(TestMetadata metadata,
                TestProperties properties) {
            // nothing to do here

        }

        public void testRunStarting(TestMetadata metadata,
                TestProperties properties, int callCount) {
            // nothing to do there
        }
    }

    public static void main(String[] args) {
        TestRunner.run(ConformanceTestSample.class);
    }

}
