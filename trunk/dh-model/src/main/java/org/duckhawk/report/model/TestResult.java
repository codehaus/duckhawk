package org.duckhawk.report.model;

import java.util.HashMap;
import java.util.Map;

public class TestResult {

    double time;

    String failureMessage;

    Test test;

    TestRun testRun;

    Map<String, String> testProperties;

    protected TestResult() {
    }
    
    public TestResult(Test test, TestRun testRun) {
        this.test = test;
        this.testRun = testRun;
    }

    public TestResult(double time, String failureMessage, Test test,
            TestRun testRun) {
        this.time = time;
        this.test = test;
        this.failureMessage = failureMessage;
        this.testRun = testRun;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public Map<String, String> getTestProperties() {
        if (testProperties == null)
            testProperties = new HashMap<String, String>();
        return testProperties;
    }

    public Test getTest() {
        return test;
    }
    
    public String getIdentifier() {
        return getTestRun().getIdentifier() + "-" + test.getName();
    }

}
