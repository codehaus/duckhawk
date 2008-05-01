package org.duckhawk.junit3;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.core.TestContext;
import org.duckhawk.util.PerformanceSummarizer;

public class ConformanceTestTest extends TestCase {
    
    int countCalls;

    public void testRunOneTest() {
        PerformanceSummarizer summarizer = new PerformanceSummarizer();
        TestContext context = new TestContext("product", "0.1", null, summarizer);
        ConformanceTest test = new ConformanceTest(context) {
            public void testStuff() {
                countCalls++;
            };
        };
        test.setName("testStuff");
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        
        // make sure the test has been run 1 time only (no warmup for conformance)
        assertEquals(1, countCalls);
        assertEquals(1, summarizer.getCallCount());
    }
}
