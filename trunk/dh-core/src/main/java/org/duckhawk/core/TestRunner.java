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
