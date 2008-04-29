package org.duckhawk.report.model;

import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;

/**
 * @author   Andrea Aime (TOPP)
 */
public class TestCallDetail {

    /**
     * @uml.property  name="id"
     */
    long id;

    /**
     * @uml.property  name="time"
     */
    double time;

    /**
     * @uml.property  name="failed"
     */
    boolean failed;

    /**
     * @uml.property  name="failureMessage"
     */
    String failureMessage;

    TestResult testResult;
    
    /**
     * @uml.property  name="callProperties"
     */
    TestProperties callProperties;

    public TestCallDetail(double time, boolean failed, String failureMessage,
            TestResult testRun) {
        super();
        this.time = time;
        this.failed = failed;
        this.failureMessage = failureMessage;
        this.testResult = testRun;
    }

    /**
     * @return
     * @uml.property  name="time"
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time
     * @uml.property  name="time"
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * @return
     * @uml.property  name="failed"
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * @param failed
     * @uml.property  name="failed"
     */
    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /**
     * @return
     * @uml.property  name="failureMessage"
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * @param failureMessage
     * @uml.property  name="failureMessage"
     */
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * @return
     * @uml.property  name="testRun"
     */
    public TestResult getTestResult() {
        return testResult;
    }

    /**
     * @param testResult
     * @uml.property  name="testRun"
     */
    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    /**
     * @return
     * @uml.property  name="callProperties"
     */
    public TestProperties getCallProperties() {
        if(callProperties == null)
            callProperties = new TestPropertiesImpl();
        return callProperties;
    }

    /**
     * @return
     * @uml.property  name="id"
     */
    public long getId() {
        return id;
    }

}
