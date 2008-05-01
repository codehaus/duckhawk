package org.duckhawk.core;

/**
 * Provides call backs for test suite wide start and end.
 * <p>
 * Typical suite listeners are reused for multiple test runners, and may need to
 * prepare and clean persistent resources.
 * <p>
 * Such listeners must not expect start and stop events to be triggered in all
 * cases, so they should be ready to fall on the first test starting event to
 * make sure resources are prepared, and use a shutdown hook (see
 * {@link Runtime#addShutdownHook(Thread)} to make sure resources are closed for
 * good.
 * <p>
 * One example of events not being triggered is when using the JUnit3
 * integration and running the tests from a build or from an IDE directly
 * (leaving DuckHawk no control over the set of tests being run), in this case
 * no start or end event will be fired.
 * <p>
 * If the listener is not able to prepare alternate means of set up and clean up
 * it should mention it in the javadoc, and be used only along with a DuckHawk
 * centralized test suite runner.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestSuiteListener {

    /**
     * This event marks the start of the whole test suite run.
     */
    public void testSuiteStarting(TestContext context);

    /**
     * This event marks the end of the whole test suite run
     */
    public void testSuiteCompleted(TestContext context);

}
