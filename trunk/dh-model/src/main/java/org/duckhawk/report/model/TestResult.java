/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.duckhawk.report.model;

import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;

/**
 * @author   Andrea Aime (TOPP)
 */
public class TestResult {
    
    Test test;

    TestRun testRun;

    /**
     * @uml.property  name="time"
     */
    double time;

    /**
     * @uml.property  name="failureMessage"
     */
    String failureMessage;

    /**
     * @uml.property  name="testProperties"
     */
    TestProperties testProperties;

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
    public TestRun getTestRun() {
        return testRun;
    }

    /**
     * @param testRun
     * @uml.property  name="testRun"
     */
    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    /**
     * @return
     * @uml.property  name="testProperties"
     */
    public TestProperties getTestProperties() {
        if (testProperties == null)
            testProperties = new TestPropertiesImpl();
        return testProperties;
    }

    /**
     * @return
     * @uml.property  name="test"
     */
    public Test getTest() {
        return test;
    }
    
    public String getIdentifier() {
        return getTestRun().getIdentifier() + "-" + test.getName();
    }

}
