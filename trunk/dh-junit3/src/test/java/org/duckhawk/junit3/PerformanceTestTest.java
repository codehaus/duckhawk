package org.duckhawk.junit3;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.junit3.PerformanceTest;

public class PerformanceTestTest extends TestCase {

    // test for DH-6
    public void testMissingSetup() {
        PerformanceTest test = new PerformanceTest("product", "0.1", 10) {
            protected void setUp() throws Exception {
                // we don't call back setup
            }

            public void testStuff() {
                // System.out.println("Hey!");
            };

            protected void tearDown() throws Exception {
                // we don't call back teardown
            }
        };
        test.setName("testStuff");
        // use this one for debugging purposes
        // TestRunner.run(test);
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
    }
}
