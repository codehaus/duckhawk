package org.duckhawk.junit3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

class JUnitTestExecutor implements TestExecutor {

    protected Method runMethod;

    protected Method checkMethod;

    protected TestCase test;

    public JUnitTestExecutor(TestCase test, Method method) {
        this.test = test;
        this.runMethod = method;
        this.runMethod.setAccessible(true);
        try {
            String checkMethodName = "check" + method.getName().substring(4);
            checkMethod = test.getClass().getDeclaredMethod(checkMethodName,
                    new Class[0]);
            checkMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // ok, fine, no checking
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
                ((PropertyTest) test).fillProperties(callProperties);
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
                ((PropertyTest) test).fillProperties(callProperties);
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
        return runMethod.getDeclaringClass().getName() + "#"
                + runMethod.getName();
    }

}
