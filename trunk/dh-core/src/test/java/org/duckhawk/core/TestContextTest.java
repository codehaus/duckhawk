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

package org.duckhawk.core;

import junit.framework.TestCase;

public class TestContextTest extends TestCase {

    public void testMissingProperties() {
        try {
            new DefaultTestContext(null, "version", null);
            fail("This should have failed, product is missing");
        } catch (Exception e) {
            // fine
        }

        try {
            new DefaultTestContext("product", null, null);
            fail("This should have failed, version is missing");
        } catch (Exception e) {
            // fine
        }
        
        // provide no listeners and no properties, should not break anyways
        new DefaultTestContext("product", "version", null, null);
    }
}
