package org.duckhawk.junit3;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.util.PerformanceSummarizer;

public class ConformanceTestTest extends TestCase {
    
    int countCalls;

    public void testRunOneTest() {
        PerformanceSummarizer summarizer = new PerformanceSummarizer();
        ConformanceTest test = new ConformanceTest("product", "0.1", summarizer) {
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
