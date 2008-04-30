package org.duckhawk.core;

/**
 * Interface implemented by classes that need to be notified of test run
 * outcomes.
 * <p>
 * Typical implementors may be test outcome storage engines, summarizers, or on
 * the fly reporting classes.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestListener {
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

    /**
     * This event marks the end of the whole test suite run. If a listener is
     * reused for multiple test runners, this can come in handy to close
     * persistent resources the listener might hang onto.
     * <p>
     * Yet, such listeners must not expect this event to be triggered in all
     * cases, so they should use a shutdown hooks (see
     * {@link Runtime#addShutdownHook(Thread)} to make sure resources are closed
     * for good.
     * <p>
     * One exemplar case is using the JUnit3 integration and running the tests
     * from a build or from an IDE directly (leaving DuckHawk no control over
     * the set of tests being run), in this case no end event will be fired.
     */
    public void testSuiteCompleted();
}
