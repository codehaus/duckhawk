package org.duckhawk.junit3;

/**
 * Implemented by tests that one can launch and then try to dispose of
 * @author Andrea Aime (TOPP)
 *
 */
public interface CancellableTest {
    /**
     * Tries to stop the execution of the current test case
     */
    public void cancel();
}
