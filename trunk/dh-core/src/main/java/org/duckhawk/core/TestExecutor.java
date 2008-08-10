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
 * Executes task to be tested and timed once.
 * <p>
 * The run method shall be stateless, that is, it must be possible to call run
 * <code>n</code> times, each time the same operation shall be executed.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestExecutor {
    
    /**
     * The request being made during the test, if any.
     */
    public static final String KEY_DESCRIPTION = "test.description";
    
    /**
     * The request being made during the test, if any.
     */
    public static final String KEY_REQUEST = "test.request";

    /**
     * The full response got back after the request was performed, if any
     */
    public static final String KEY_RESPONSE = "test.response";

    /**
     * The name of the thread running the current request
     */
    public static final String KEY_THREAD_ID = "test.threadName";

    /**
     * The full response got back after the request was performed, if any
     */
    public static final String KEY_CALL_NUMBER = "test.callNumber";

    /**
     * The time when the call was executed, relative to the start of the test
     * suite
     */
    public static final String KEY_CALL_TIME = "test.callTimeMs";

    /**
     * The kind of test being run
     */
    public static final String KEY_TEST_TYPE = "test.testType";

    /**
     * If the test is doing any post processing, this item can store the actual
     * request time (key should be a Double)
     */
    public static final String KEY_CALL_COUNT = "test.callCount";

    /**
     * The minimum run time for any of the calls to the same
     * {@link TestExecutor} during a test run
     */
    public static final String KEY_MIN_TIME = "perf.minTime";

    /**
     * The maximum run time for any of the calls to the same
     * {@link TestExecutor} during a test run
     */
    public static final String KEY_MAX_TIME = "perf.maxTime";

    /**
     * The average run time for any of the calls to the same
     * {@link TestExecutor} during a test run
     */
    public static final String KEY_AVG_TIME = "perf.averageTime";

    /**
     * The median run time for any of the calls to the same {@link TestExecutor}
     * during a test run
     */
    public static final String KEY_MED_TIME = "perf.medianTime";

    /**
     * The sum of all times for any of the calls to the same
     * {@link TestExecutor} during a test run
     */
    public static final String KEY_TOTAL_TIME = "perf.totalTime";

    /**
     * A summary of the error messages collected during the {@link TestExecutor}
     * test run
     */
    public static final String KEY_ERROR_SUMMARY = "conf.errorSummary";

    /**
     * The number of errors occurred during the {@link TestExecutor} test run
     */
    public static final String KEY_ERROR_COUNT = "conf.errorCount";

    /**
     * The percentage of test calls that resulted in an error during the
     * {@link TestExecutor} test run
     */
    public static final String KEY_ERROR_PERCENTAGE = "conf.errorPercentage";

    /**
     * Gives the test an occasion to initialize itself by:
     * <ul>
     * <li>Reading properties from the test environment (the provided
     * environment variable is to be assumed read only)</li>
     * <li>Fill in the test properties for the whole run (they will be reported
     * along with the test starting events)</li>
     * 
     * @param testProperties
     */
    public void init(TestProperties environment, TestProperties testProperties);

    /**
     * Executes the test and fills in the eventual test properties for this test
     * run (a list of well known test run property keys is contained in this
     * class, but each test can add whatever it wants to it)
     * 
     * @throws Throwable
     * @returns
     */
    public void run(TestProperties callProperties) throws Throwable;

    /**
     * This method is called after run. Run is the timed part of the execution,
     * check can take as much time as it sees fit to perform validity checks on
     * the result without affecting the timing, but only the conformance of this
     * test run
     * 
     * @param callProperties
     * @throws Throwable
     */
    public void check(TestProperties callProperties) throws Throwable;

    /**
     * Forcefully stops the current thread
     */
    public void cancel() throws Throwable;

    /**
     * Returns a deep copy of the executor, should be each bit equal to the
     * original one, but completely independent of it
     * 
     * @return
     */
    public TestExecutor cloneExecutor();

    /**
     * Provides an identification for this test
     * 
     * @return
     */
    public String getTestId();

}
