package org.duckhawk.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestContext {

    public enum TestSuiteState {
        /**
         * Ready to run, but suite listeners still haven't been informed that
         * the suite is running
         */
        ready,
        /**
         * The suite is running, test suite listeners have been informed of the
         * start already
         */
        running,
        /**
         * Test suite done, the test suite listeners have been informed
         */
        complete
    };

    TestProperties environment;

    List<TestListener> listeners;

    String productId;

    String productVersion;

    TestSuiteState state;

    public TestContext(String productId, String productVersion,
            TestProperties environment, TestListener... listeners) {
        if (productId == null)
            throw new IllegalArgumentException("ProductId not specified");
        if (productVersion == null)
            throw new IllegalArgumentException("VersionId not specified");
        this.environment = environment == null ? new TestPropertiesImpl()
                : environment;
        this.listeners = Collections.unmodifiableList(Arrays.asList(listeners));
        this.productId = productId;
        this.productVersion = productVersion;
        this.state = TestSuiteState.ready;
    }

    public TestProperties getEnvironment() {
        // TODO: make this un-modifiable as well?
        return environment;
    }

    public List<TestListener> getListeners() {
        return listeners;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Notifies all test suite listeners that the test suite is about to start
     */
    public synchronized void fireTestSuiteStarting() {
        try {
            for (TestListener listener : listeners) {
                if (listener instanceof TestSuiteListener)
                    ((TestSuiteListener) listener).testSuiteStarting(this);
            }
        } finally {
            state = TestSuiteState.running;
        }
    }

    /**
     * Notifies all test suite listeners that the test suite is ending
     */
    public synchronized void fireTestSuiteEnding() {
        try {
            for (TestListener listener : listeners) {
                if (listener instanceof TestSuiteListener)
                    ((TestSuiteListener) listener).testSuiteCompleted(this);
            }
        } finally {
            state = TestSuiteState.complete;
        }
    }

    /**
     * Current state of the test suite
     * 
     * @return
     */
    public TestSuiteState getState() {
        return state;
    }

    /**
     * Resets this context for reuse (sets back the state to
     * {@link TestSuiteState#ready}).
     */
    public void reset() {
        this.state = TestSuiteState.ready;
    }
}
