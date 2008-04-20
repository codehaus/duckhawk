package org.duckhawk.core;

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
     * Called when a {@link TestExecutor} a single call completed.
     * 
     * @param event
     */
    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            double time, Throwable exception);

    /**
     * Called when the test run is about to start
     * 
     * @param metadata
     *                Identification data for this test run
     * @param callNumber
     *                The expected number of times
     *                {@link #testCallExecuted(TimedTestEvent)} will be called
     *                back
     */
    public void testRunStarting(TestMetadata metadata, int callNumber);

    /**
     * This event is issued when a test run is completed
     * 
     * @param metadata
     *                Identification data for this test run
     */
    public void testRunCompleted(TestMetadata metadata);
}
