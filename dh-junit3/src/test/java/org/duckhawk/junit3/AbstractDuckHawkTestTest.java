package org.duckhawk.junit3;

import junit.framework.TestCase;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestRunner;

public class AbstractDuckHawkTestTest extends TestCase {
    
    private TestContext context;

    @Override
    protected void setUp() throws Exception {
        context = new TestContext("product", "version", null);
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
