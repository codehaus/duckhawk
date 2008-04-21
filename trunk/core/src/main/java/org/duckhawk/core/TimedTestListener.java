package org.duckhawk.core;

import java.util.Map;

/**
 * Interface implemented by classes that need to be notified of test run
 * outcomes.
 * <p>
 * Typical implementors may be test outcome storage engines or on the fly
 * reporting classes.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TimedTestListener {
    /**
     * Called when a {@link TestExecutor} single call completed.
     * 
     * @param executor
     *                The executor that did run the test
     * @param metadata
     *                Identification data for this test run and test properties.
     * @param callProperties
     *                The properties attached to this call by the
     *                {@link TestExecutor} itself during the run, along with any
     *                other properties the other listeners in the listening
     *                chain might have added/modified/removed. <br>
     *                These properties are cleared out and recomputed for each
     *                call, they are not stateful.
     * @param time
     *                The time the test took to execute, in seconds
     * @param exception
     *                The eventual exception thrown while the test was running
     */
    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception);

    /**
     * Called when the test run is about to start
     * 
     * @param metadata
     *                Identification data for this test run and test properties.
     * @param callNumber
     *                The expected number of times
     *                {@link #testCallExecuted(TimedTestEvent)} will be called
     *                back
     * @param testProperties
     *                The test properties are built by the
     *                {@link TestExecutorFactory} and are kept alive during all
     *                the test run. Listeners might want to use or modify them.
     */
    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber);

    /**
     * This event is issued when a test run is completed
     * 
     * @param metadata
     *                Identification data for this test run and test properties.
     * @param testProperties
     *                The test properties are built by the
     *                {@link TestExecutorFactory} and are kept alive during all
     *                the test run. Listeners might want to use or modify them.
     * 
     */
    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties);
}
