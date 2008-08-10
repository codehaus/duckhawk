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
 * Executes the test provided by the {@link TestExecutorFactory}.
 * <p>
 * The runner is supposed to operate in a {@link DefaultTestContext} and operate
 * against a {@link TestExecutor} that actually performs whatever action needs
 * checking or timing.
 * <p>
 * Both context and executor are provided along in the runner constructor
 * 
 * @author Andrea Aime (TOPP)
 */
public interface TestRunner {

    /**
     * Runs the tests and notified the listeners of the progress
     * 
     * @param factory
     */
    public void runTests();

    /**
     * The test context in which this runner is going to operate
     * 
     * @return
     */
    public TestContext getContext();

    /**
     * The test executor used by this runner to perform its activities
     * 
     * @return
     */
    public TestExecutor getTestExecutor();
    
    /**
     * Returns the test type carried on by this executor
     * @return
     */
    public TestType getTestType();

    /**
     * Disposes of the runner (should the runner need any resource that needs
     * freeing before ending its life). Once disposed the TestRunner is not
     * guaranteed to be usable anymore.
     */
    public void dispose();

    /**
     * Forcefully stops the current test run
     */
    public void cancel();
}
