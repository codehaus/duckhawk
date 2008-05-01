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
