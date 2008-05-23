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
