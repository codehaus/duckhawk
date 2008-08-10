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

import java.util.List;

import org.duckhawk.core.DefaultTestContext.TestSuiteState;

public interface TestContext {

    TestProperties getEnvironment();

    List<TestListener> getListeners();

    String getProductId();

    String getProductVersion();

    /**
     * Notifies all test suite listeners that the test suite is about to start
     */
    void fireTestSuiteStarting();

    /**
     * Notifies all test suite listeners that the test suite is ending
     */
    void fireTestSuiteEnding();

    /**
     * Current state of the test suite
     * 
     * @return
     */
    TestSuiteState getState();

    /**
     * Resets this context for reuse (sets back the state to
     * {@link TestSuiteState#ready}).
     */
    void reset();

    /**
     * Returns the time when the test suite started (according to
     * {@link System#currentTimeMillis()} when the start suite event was fired)
     * 
     * @return
     */
    long getStart();

    /**
     * Returns the time when the test suite ended (according to
     * {@link System#currentTimeMillis()} when the end suite event was fired)
     * 
     * @return
     */
    long getEnd();

}
