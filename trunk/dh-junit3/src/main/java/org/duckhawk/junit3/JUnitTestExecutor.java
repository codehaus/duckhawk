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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

class JUnitTestExecutor implements TestExecutor {
    private static final Log LOGGER = LogFactory
            .getLog(JUnitTestExecutor.class);

    protected Method runMethod;

    protected Method checkMethod;

    protected Method initMethod;

    protected TestCase test;
    
    protected String testMethodsSuffix;
    
    protected String testClassSuffix;

    public JUnitTestExecutor(TestCase test, Method method) {
        this(test, method, null, null);
    }
    
    /**
     * Initializes the test executor
     * @param test
     * @param method
     * @param testMethodsSuffix
     */
    public JUnitTestExecutor(TestCase test, Method method, String testClassSuffix, String testMethodsSuffix) {
        this.testMethodsSuffix = testMethodsSuffix;
        this.testClassSuffix = testClassSuffix;
        this.test = test;
        this.runMethod = method;
        this.runMethod.setAccessible(true);

        // look up for the method that will be used for performing test property
        // initialization
        String initMethodName = "init" + method.getName().substring(4);
        try {
            initMethod = test.getClass().getMethod(initMethodName,
                    new Class[] { TestProperties.class });
            initMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.debug("Could not find init method " + initMethodName
                    + " with TestProperties param, skipping init");
            // ok, fine, no test properties init
        }

        // look up for the method that will be used for performing un-timed
        // checks
        String checkMethodName = "check" + method.getName().substring(4);
        try {
            checkMethod = test.getClass().getMethod(checkMethodName);
            checkMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            LOGGER.debug("Could not find check method " + checkMethodName + " skipping check");
        }
    }

    public void run(TestProperties callProperties) throws Throwable {
        Exception currentException = null;
        try {
            runMethod.invoke(test);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            currentException = e;
            throw e.getTargetException();
        } finally {
            if (test instanceof PropertyTest
                    && (currentException != null || checkMethod == null)) {
                ((PropertyTest) test).fillCallProperties(callProperties);
            }
        }
    }

    public void check(TestProperties callProperties) throws Throwable {
        if (checkMethod == null)
            return;

        try {
            checkMethod.invoke(test);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw e.getTargetException();
        } finally {
            if (test instanceof PropertyTest) {
                ((PropertyTest) test).fillCallProperties(callProperties);
            }
        }
    }

    public void cancel() throws Throwable {
        if (test instanceof CancellableTest)
            ((CancellableTest) test).cancel();
    }

    public TestExecutor cloneExecutor() {
        return new JUnitTestExecutor(test, runMethod);
    }

    /**
     * Test id is the class name plus method name
     */
    public String getTestId() {
        String className = test.getClass().getName() + (testClassSuffix != null ? testClassSuffix : "");
        String methodName = runMethod.getName() + (testMethodsSuffix != null ? testMethodsSuffix : "");
        return className + "#" + methodName;
    }

    public void init(TestProperties environment, TestProperties testProperties) {
        if (test instanceof PropertyTest)
            ((PropertyTest) test).initEnviroment(environment);

        if (initMethod == null)
            return;

        try {
            initMethod.invoke(test, testProperties);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw new RuntimeException(e.getTargetException());
        } catch (Throwable t) {
            throw new RuntimeException("Unexpected exception occurred "
                    + "during executor initialization", t);
        }
    }

}
