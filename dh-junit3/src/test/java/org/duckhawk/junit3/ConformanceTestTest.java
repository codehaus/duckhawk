package org.duckhawk.junit3;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.util.PerformanceSummarizer;

public class ConformanceTestTest extends TestCase {
    int countCalls;
    int countInits;
    TestContext context;
    int executedCount;
    int completedCount;
    TestProperties runStartingProps;
    int startingCount;
    
    private class RecordingListener implements TestListener {

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, TestProperties callProperties,
                double time, Throwable exception) {
            executedCount++;
        }

        public void testRunCompleted(TestMetadata metadata,
                TestProperties testProperties) {
            completedCount++;
        }

        public void testRunStarting(TestMetadata metadata,
                TestProperties testProperties, int callNumber) {
            startingCount++;
            runStartingProps = new TestPropertiesImpl();
            runStartingProps.putAll(testProperties);
        }
        
    }
    
    @Override
    protected void setUp() throws Exception {
        context = new TestContext("product", "0.1", null, new RecordingListener());
    }

    public void testRunOneTest() {
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
        assertEquals(1, executedCount);
    }
    
    public void testRunWithInits() {
        ConformanceTest test = new ConformanceTest(context) {
            public void testStuff() throws InterruptedException {
                countCalls++;
            };
            
            public void initStuff(TestProperties testProperties) throws InterruptedException {
                countInits++;
                testProperties.put("test", "I was here!");
            };
        };
        test.setName("testStuff");
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        
        // make sure the test has been init 1 times and run one time 
        assertEquals(1, countCalls);
        assertEquals(1, countInits);
        // check the listener records
        assertEquals(1, executedCount);
        assertEquals(1, startingCount);
        assertEquals(1, completedCount);
        // the test runner will put in the call count
        assertEquals(2, runStartingProps.size());
        assertEquals("I was here!", runStartingProps.get("test"));
    }
}
