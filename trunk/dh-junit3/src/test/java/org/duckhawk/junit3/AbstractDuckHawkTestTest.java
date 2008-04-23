package org.duckhawk.junit3;

import junit.framework.TestCase;

import org.duckhawk.core.TestRunner;
import org.easymock.EasyMock;

public class AbstractDuckHawkTestTest extends TestCase {

    public void testMissingProperties() {
        try {
            new AbstractDuckHawkTest(null, "version") {
                protected TestRunner getTestRunner() {
                    return null;
                }
            };
            fail("This should have failed, product is missing");
        } catch (Exception e) {
            // fine
        }

        try {
            new AbstractDuckHawkTest("product", null) {
                protected TestRunner getTestRunner() {
                    return null;
                }
            };
            fail("This should have failed, version is missing");
        } catch (Exception e) {
            // fine
        }
    }

    public void testMissingMethod() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest("product",
                "version") {
            protected TestRunner getTestRunner() {
                return null;
            }
        };
        test.setName("testNotThere");
        assertEquals(1, test.run().failureCount());
    }
    
    public void testPrivateMethod() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest("product",
                "version") {
            protected TestRunner getTestRunner() {
                return null;
            }
            
            private void testTopSecret() {}
        };
        test.setName("testTopSecret");
        assertEquals(1, test.run().failureCount());
    }
    
    public void testMinimal() {
        AbstractDuckHawkTest test = new AbstractDuckHawkTest("product",
                "version") {
            protected TestRunner getTestRunner() {
                return EasyMock.createNiceMock(TestRunner.class);
            }
            
            public void testMe() {}
        };
        test.setName("testMe");
        assertEquals(0, test.run().failureCount());
        assertEquals(0, test.run().errorCount());
    }
}
