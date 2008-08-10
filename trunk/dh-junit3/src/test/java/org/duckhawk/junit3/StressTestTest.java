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
import org.duckhawk.util.PerformanceSummarizer;

public class StressTestTest extends TestCase {
    
    private PerformanceSummarizer summarizer;
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        summarizer = new PerformanceSummarizer();
        context = new DefaultTestContext("product", "0.1", null, summarizer);
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
