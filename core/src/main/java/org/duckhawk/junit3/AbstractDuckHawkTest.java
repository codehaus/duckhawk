package org.duckhawk.junit3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestExecutorFactory;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TimedTestRunner;

public abstract class AbstractDuckHawkTest extends TestCase implements PropertyTest {

    /**
     * The product id for this test run
     */
    protected String productId;

    /**
     * The product version for this test run
     */
    protected String productVersion;

    /**
     * The properties for the single test run. <br>
     * They are cleared each time {@link #setUp()} is called, and they are
     * supposed to gather properties specific to the test method being run.
     */
    protected TestProperties properties;

    public AbstractDuckHawkTest(String productId, String productVersion) {
        if (productId == null)
            throw new IllegalArgumentException("ProductId not specified");
        if (productVersion == null)
            throw new IllegalArgumentException("VersionId not specified");
        this.productId = productId;
        this.productVersion = productVersion;
    }

    @Override
    protected void setUp() throws Exception {
        if (properties == null)
            properties = new TestPropertiesImpl();
        properties.clear();
    }
    
    public void fillProperties(TestProperties callProperties) {
        callProperties.putAll(properties);
    }

    protected abstract TimedTestRunner getTestRunner();

    @Override
    protected void runTest() throws Throwable {
        TimedTestRunner runner = getTestRunner();
        runner
                .evaluatePerformance(new JUnitTestExecutorFactory(
                        getRunMethod()));
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

        private TestMetadata metadata;

        public JUnitTestExecutorFactory(Method runMethod) {
            this.runMethod = runMethod;
            String id = runMethod.getDeclaringClass().getName() + "."
                    + runMethod.getName() + "()";
            this.metadata = new TestMetadata(id, productId, productVersion);
        }

        /**
         * Grabs the test method to be run and packs it into a TestExecutor
         * 
         * @return
         */
        public TestExecutor createTestExecutor() {
            return new JUnitTestExecutor(AbstractDuckHawkTest.this, runMethod);
        }

        public TestMetadata createMetadata() {
            return metadata;
        }

    }

}
