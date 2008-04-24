package org.duckhawk.report.model;

import java.util.Map;

public class TestCallDetail {

    long id;

    double time;

    boolean failed;

    String failureMessage;

    TestResult testRun;

    Map<String, String> callProperties;

    public TestCallDetail(double time, boolean failed, String failureMessage,
            TestResult testRun) {
        super();
        this.time = time;
        this.failed = failed;
        this.failureMessage = failureMessage;
        this.testRun = testRun;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public TestResult getTestRun() {
        return testRun;
    }

    public void setTestRun(TestResult testRun) {
        this.testRun = testRun;
    }

    public Map<String, String> getCallProperties() {
        return callProperties;
    }

    public void setCallProperties(Map<String, String> callProperties) {
        this.callProperties = callProperties;
    }

    public long getId() {
        return id;
    }

}
