package org.duckhawk.junit3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

class JUnitTestExecutor implements TestExecutor {

    Method method;

    TestCase test;

    public JUnitTestExecutor(TestCase test, Method method) {
        this.test = test;
        this.method = method;
    }

    public void run(TestProperties properties) throws Throwable {
        try {
            method.invoke(test);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            e.fillInStackTrace();
            throw e;
        } finally {
            if(test instanceof PropertyTest) {
                ((PropertyTest) test).fillProperties(properties);
            }
        }
    }

}
