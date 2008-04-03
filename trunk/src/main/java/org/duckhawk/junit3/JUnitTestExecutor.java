package org.duckhawk.junit3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;

class JUnitTestExecutor implements TestExecutor {

    Method method;

    String product;

    String productVersion;

    TestCase test;

    public JUnitTestExecutor(TestCase test, Method method, String product, String productVersion) {
        this.test = test;
        this.method = method;
        this.product = product;
        this.productVersion = productVersion;
    }

    public void run() throws Throwable {
        try {
            method.invoke(test);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            e.fillInStackTrace();
            throw e;
        }
    }

    /**
     * The testId of a Junit test run is the fully qualified name of the class
     * followed by the method name
     */
    public String getTestId() {
        return method.getDeclaringClass().getName() + "." + method.getName() + "()";
    }

    public String getProductId() {
        return product;
    }

    public String getProductVersion() {
        return productVersion;
    }

}
