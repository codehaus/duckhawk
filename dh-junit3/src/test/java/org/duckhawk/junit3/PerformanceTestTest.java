/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.duckhawk.junit3;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.DefaultTestContext;
import org.duckhawk.core.TestProperties;
import org.duckhawk.util.PerformanceSummarizer;

public class PerformanceTestTest extends TestCase {
    
    int countCalls;
    int countChecks;
    TestContext context;
    PerformanceSummarizer summarizer;
    
    @Override
    protected void setUp() throws Exception {
        countCalls = 0;
        countChecks = 0;
        summarizer = new PerformanceSummarizer();
        context = new DefaultTestContext("product", "0.1", null, summarizer);
    }

    // test for DH-6
    public void testMissingSetup() {
        PerformanceTest test = new PerformanceTest(context, 10) {
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
    
    public void testRunOneTest() {
        PerformanceTest test = new PerformanceTest(context, 10) {
            public void testStuff() {
                countCalls++;
            };
        };
        test.setName("testStuff");
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        
        // make sure the test has been run 10 times + 1 for the warmup
        assertEquals(11, countCalls);
        assertEquals(10, summarizer.getCallCount());
    }
    
    public void testRunTwoTest() {
        PerformanceTest test = new PerformanceTest(context, 10) {
            public void testStuff1() {
                countCalls++;
            };
            
            public void testStuff2() {
                countCalls++;
            };
        };
        test.setName("testStuff1");
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        // make sure the test has been run 10 times + 1 for the warmup, two times
        assertEquals(11, countCalls);
        assertEquals(10, summarizer.getCallCount());
        
        test.setName("testStuff2");
        result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        
        // count has accumulated, the summarizer should have been restarted instead
        assertEquals(22, countCalls);
        assertEquals(10, summarizer.getCallCount());
    }
    
    public void testRunWithChecks() {
        PerformanceTest test = new PerformanceTest(context, 1) {
            public void testStuff() throws InterruptedException {
                Thread.sleep(200);
                countCalls++;
            };
            
            public void checkStuff() throws InterruptedException {
                Thread.sleep(200);
                countChecks++;
            };
        };
        test.setName("testStuff");
        TestResult result = test.run();
        assertEquals(0, result.errorCount());
        assertEquals(0, result.failureCount());
        
        // make sure the test has been run 1 times + 1 for the warmup, two times
        assertEquals(2, countCalls);
        assertEquals(2, countChecks);
        assertEquals(1, summarizer.getCallCount());
        // make sure only the testStuff method has been timed
        assertEquals(0.2, summarizer.getMedian(), 0.01);
    }
    
}
