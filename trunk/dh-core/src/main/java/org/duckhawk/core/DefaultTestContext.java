package org.duckhawk.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultTestContext implements TestContext {

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

    private long start;

    private long end;

    public DefaultTestContext(String productId, String productVersion,
            TestProperties environment, TestListener... listeners) {
        if (productId == null)
            throw new IllegalArgumentException("ProductId not specified");
        if (productVersion == null)
            throw new IllegalArgumentException("VersionId not specified");
        this.environment = environment == null ? new TestPropertiesImpl()
                : environment;
        if(listeners != null)
            this.listeners = Collections.unmodifiableList(Arrays.asList(listeners));
        else
            this.listeners = Collections.emptyList();
        this.productId = productId;
        this.productVersion = productVersion;
        this.state = TestSuiteState.ready;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getEnvironment()
     */
    public TestProperties getEnvironment() {
        // TODO: make this un-modifiable as well?
        return environment;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getListeners()
     */
    public List<TestListener> getListeners() {
        return listeners;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getProductId()
     */
    public String getProductId() {
        return productId;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getProductVersion()
     */
    public String getProductVersion() {
        return productVersion;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#fireTestSuiteStarting()
     */
    public synchronized void fireTestSuiteStarting() {
        start = System.currentTimeMillis();
        try {
            for (TestListener listener : listeners) {
                if (listener instanceof TestSuiteListener)
                    ((TestSuiteListener) listener).testSuiteStarting(this);
            }
        } finally {
            state = TestSuiteState.running;
        }
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#fireTestSuiteEnding()
     */
    public synchronized void fireTestSuiteEnding() {
        end = System.currentTimeMillis();
        try {
            for (TestListener listener : listeners) {
                if (listener instanceof TestSuiteListener)
                    ((TestSuiteListener) listener).testSuiteCompleted(this);
            }
        } finally {
            state = TestSuiteState.complete;
        }
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getState()
     */
    public TestSuiteState getState() {
        return state;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#reset()
     */
    public void reset() {
        this.state = TestSuiteState.ready;
    }

    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getStart()
     */
    public long getStart() {
        return start;
    }
    
    /* (non-Javadoc)
     * @see org.duckhawk.core.ITestContext#getEnd()
     */
    public long getEnd() {
        return end;
    }
}
