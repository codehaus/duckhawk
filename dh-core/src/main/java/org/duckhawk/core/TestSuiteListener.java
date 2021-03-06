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

/**
 * Provides call backs for test suite wide start and end.
 * <p>
 * Typical suite listeners are reused for multiple test runners, and may need to
 * prepare and clean persistent resources.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestSuiteListener extends TestListener {

    /**
     * This event marks the start of the whole test suite run.
     */
    public void testSuiteStarting(TestContext context);

    /**
     * This event marks the end of the whole test suite run
     */
    public void testSuiteCompleted(TestContext context);

}
