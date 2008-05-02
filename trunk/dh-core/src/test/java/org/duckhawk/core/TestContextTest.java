package org.duckhawk.core;

import junit.framework.TestCase;

public class TestContextTest extends TestCase {

    public void testMissingProperties() {
        try {
            new TestContext(null, "version", null);
            fail("This should have failed, product is missing");
        } catch (Exception e) {
            // fine
        }

        try {
            new TestContext("product", null, null);
            fail("This should have failed, version is missing");
        } catch (Exception e) {
            // fine
        }
        
        // provide no listeners and no properties, should not break anyways
        new TestContext("product", "version", null, null);
    }
}
