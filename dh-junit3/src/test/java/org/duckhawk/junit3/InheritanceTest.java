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

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;

public class InheritanceTest extends TestCase {

    private static class AbstractTest extends TestCase {
        boolean initFooCalled;
        boolean testFooCalled;
        boolean checkFooCalled;

        public void initFoo(TestProperties props) {
            initFooCalled = true;
        }

        public void testFoo() {
            testFooCalled = true;
        }

        public void checkFoo() {
            checkFooCalled = true;
        }
    }
    
    private static class DerivedTest extends AbstractTest {
        
    }
    
    private static class OvveridingTest extends AbstractTest {
        
        boolean overriddenCheckFooCalled;

        public void checkFoo() {
            overriddenCheckFooCalled = true;
        }
    }
    
    public void testCheckId() throws Exception {
        TestCase tc = new DerivedTest();
        Method m = tc.getClass().getMethod("testFoo");
        JUnitTestExecutor executor = new JUnitTestExecutor(tc, m);
        
        assertEquals(DerivedTest.class.getName() + "#testFoo", executor.getTestId());
    }
    
    public void testInheritedMethodCalls() throws Throwable {
        DerivedTest tc = new DerivedTest();
        Method m = tc.getClass().getMethod("testFoo");
        JUnitTestExecutor executor = new JUnitTestExecutor(tc, m);
        
        executor.init(new TestPropertiesImpl(), new TestPropertiesImpl());
        executor.run(new TestPropertiesImpl());
        executor.check(new TestPropertiesImpl());
        
        assertTrue(tc.testFooCalled);
        assertTrue(tc.initFooCalled);
        assertTrue(tc.checkFooCalled);
    }
    
    public void testOverriddenMethodCalls() throws Throwable {
        OvveridingTest tc = new OvveridingTest();
        Method m = tc.getClass().getMethod("testFoo");
        JUnitTestExecutor executor = new JUnitTestExecutor(tc, m);
        
        executor.init(new TestPropertiesImpl(), new TestPropertiesImpl());
        executor.run(new TestPropertiesImpl());
        executor.check(new TestPropertiesImpl());
        
        assertTrue(tc.testFooCalled);
        assertTrue(tc.initFooCalled);
        assertFalse(tc.checkFooCalled);
        assertTrue(tc.overriddenCheckFooCalled);
    }
}
