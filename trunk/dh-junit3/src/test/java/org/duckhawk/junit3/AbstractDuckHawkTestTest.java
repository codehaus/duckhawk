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

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.DefaultTestContext;
import org.duckhawk.core.TestRunner;

public class AbstractDuckHawkTestTest extends TestCase {
    
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        context = new DefaultTestContext("product", "version", null);
    }

    public void testMissingMethod() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest(context) {
            protected TestRunner getTestRunner(TestContext context) {
                return new ConformanceTestRunner(context, buildTestExecutor());
            }
        };
        test.setName("testNotThere");
        assertEquals(1, test.run().failureCount());
    }
    
    public void testPrivateMethod() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest(context) {
            protected TestRunner getTestRunner(TestContext context) {
                return new ConformanceTestRunner(context, buildTestExecutor());
            }
            
            private void testTopSecret() {}
        };
        test.setName("testTopSecret");
        assertEquals(1, test.run().failureCount());
    }
    
    public void testMinimal() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest(context) {
            protected TestRunner getTestRunner(TestContext context) {
                return new ConformanceTestRunner(context, buildTestExecutor());
            }
            
            public void testMe() {}
        };
        test.setName("testMe");
        assertEquals(0, test.run().failureCount());
        assertEquals(0, test.run().errorCount());
    }
}
