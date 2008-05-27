package org.duckhawk.core;

import java.util.List;

import org.duckhawk.core.DefaultTestContext.TestSuiteState;

public interface TestContext {

    TestProperties getEnvironment();

    List<TestListener> getListeners();

    String getProductId();

    String getProductVersion();

    /**
     * Notifies all test suite listeners that the test suite is about to start
     */
    void fireTestSuiteStarting();

    /**
     * Notifies all test suite listeners that the test suite is ending
     */
    void fireTestSuiteEnding();

    /**
     * Current state of the test suite
     * 
     * @return
     */
    TestSuiteState getState();

    /**
     * Resets this context for reuse (sets back the state to
     * {@link TestSuiteState#ready}).
     */
    void reset();

    /**
     * Returns the time when the test suite started (according to
     * {@link System#currentTimeMillis()} when the start suite event was fired)
     * 
     * @return
     */
    long getStart();

    /**
     * Returns the time when the test suite ended (according to
     * {@link System#currentTimeMillis()} when the end suite event was fired)
     * 
     * @return
     */
    long getEnd();

}