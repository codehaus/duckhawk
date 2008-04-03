package org.duckhawk.core;

/**
 * Interface implemented by classes that need to be notified of test run outcomes.<p>
 * Typical implementors may be test outcome storage engines or on the fly reporting classes.
 * @author Andrea Aime (TOPP)
 *
 */
public interface TimedTestListener {
	/**
	 * Called when a {@link TestExecutor} run completed. In case of a multithreaded tests
	 * this method can be called concurrently, so it must be thread safe (and possibly
	 * not synchronized in order to avoid test slow down).
	 * @param event
	 */
    public void testRunExecuted(TimedTestEvent event);
}
