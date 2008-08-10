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

package org.duckhawk.junit3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.DefaultTestContext.TestSuiteState;

/**
 * Wraps the normal test context to add an extra listener that will be used by
 * the JUnit3 integration to report back the first exception occurred during the
 * execution of the tests, if any. This allows test runners to show the test has
 * had problems.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
class ExceptionContextWrapper implements TestContext {
    TestContext wrapped;

    Throwable firstException;

    List<TestListener> extendedListeners;

    int exceptionCount;

    public int getExceptionCount() {
        return exceptionCount;
    }

    public ExceptionContextWrapper(TestContext wrapped) {
        this.wrapped = wrapped;
        List<TestListener> listeners = new ArrayList<TestListener>();
        listeners.addAll(wrapped.getListeners());
        listeners.add(new ExceptionListener());
        extendedListeners = Collections.unmodifiableList(listeners);
    }

    public Throwable getFirstException() {
        return firstException;
    }

    public void fireTestSuiteEnding() {
        wrapped.fireTestSuiteEnding();
    }

    public void fireTestSuiteStarting() {
        wrapped.fireTestSuiteStarting();
    }

    public long getEnd() {
        return wrapped.getEnd();
    }

    public TestProperties getEnvironment() {
        return wrapped.getEnvironment();
    }

    public List<TestListener> getListeners() {
        return extendedListeners;
    }

    public String getProductId() {
        return wrapped.getProductId();
    }

    public String getProductVersion() {
        return wrapped.getProductVersion();
    }

    public long getStart() {
        return wrapped.getStart();
    }

    public TestSuiteState getState() {
        return wrapped.getState();
    }

    public void reset() {
        wrapped.reset();
    }

    private class ExceptionListener implements TestListener {

        public void testCallExecuted(TestExecutor executor,
                TestMetadata metadata, TestProperties callProperties,
                double time, Throwable exception) {
            if (firstException == null && exception != null) {
                firstException = exception;
            }
            if (exception != null)
                exceptionCount++;
        }

        public void testRunCompleted(TestMetadata metadata,
                TestProperties testProperties) {
            // nothing to do
        }

        public void testRunStarting(TestMetadata metadata,
                TestProperties testProperties, int callNumber) {
            // nothing to do
        }

    }
}
