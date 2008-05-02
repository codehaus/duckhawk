package org.duckhawk.junit3;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.core.TestContext;
import org.duckhawk.util.PerformanceSummarizer;

public class StressTestTest extends TestCase {
    
    private PerformanceSummarizer summarizer;
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        summarizer = new PerformanceSummarizer();
        context = new TestContext("product", "0.1", null, summarizer);
    }

    // test for DH-6
    public void testMissingSetup() {
        
        StressTest test = new StressTest(context, 10, 2, 0) {
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
        assertEquals(20, summarizer.getCallCount());
    }
}
