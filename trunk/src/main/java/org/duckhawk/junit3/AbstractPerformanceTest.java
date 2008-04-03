package org.duckhawk.junit3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestExecutorFactory;
import org.duckhawk.core.TimedTestRunner;

public abstract class AbstractPerformanceTest extends TestCase {

    protected String productId;

    protected String productVersion;

    public AbstractPerformanceTest(String productId, String productVersion) {
        if(productId == null)
            throw new IllegalArgumentException("ProductId not specified");
        if(productVersion == null)
            throw new IllegalArgumentException("VersionId not specified");
        this.productId = productId;
        this.productVersion = productVersion;
    }

    protected abstract TimedTestRunner getPerformanceTester();

    @Override
    protected void runTest() throws Throwable {
        TimedTestRunner runner = getPerformanceTester();
        runner.evaluatePerformance(new JUnitTestExecutorFactory(getRunMethod()));
    }
    
    private Method getRunMethod() {
        assertNotNull(getName());
        Method runMethod = null;
        try {
            // use getMethod to get all public inherited
            // methods. getDeclaredMethods returns all
            // methods of this class but excludes the
            // inherited ones.
            runMethod = getClass().getMethod(getName(), new Class[0]);
        } catch (NoSuchMethodException e) {
            fail("Method \"" + getName() + "\" not found");
        }
        if (!Modifier.isPublic(runMethod.getModifiers())) {
            fail("Method \"" + getName() + "\" should be public");
        }
        return runMethod;
    }
    
    private class JUnitTestExecutorFactory implements TestExecutorFactory {
        private Method runMethod;

        public JUnitTestExecutorFactory(Method runMethod) {
            this.runMethod = runMethod;
        }

        /**
         * Grabs the test method to be run and packs it into a TestExecutor
         * 
         * @return
         */
        public TestExecutor buildTestExecutor() {
            return new JUnitTestExecutor(AbstractPerformanceTest.this, runMethod, productId, productVersion);
        }

    }

    
}
