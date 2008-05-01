package org.duckhawk.junit3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.TestContext.TestSuiteState;

/**
 * The abstract integration between JUnit3 and DuckHawk. Subclasses specializes
 * it for conformance and performance tests
 * 
 * @author Andrea Aime (TOPP)
 * @uml.dependency supplier="org.duckhawk.junit3.JUnitTestExecutor"
 */
public abstract class AbstractDuckHawkTest extends TestCase implements
        PropertyTest, CancellableTest {

    /**
     * The test context in which we're running
     */
    protected TestContext context;

    /**
     * The properties for the single test run.
     */
    protected TestProperties properties;

    /**
     * If true, this tests has been cancelled
     */
    protected boolean cancelled;

    /**
     * Creates a new test with the minimum properties needed to identify a test.
     * 
     * @param productId
     * @param productVersion
     */
    public AbstractDuckHawkTest(TestContext context) {
        this.context = context;
        this.properties = new TestPropertiesImpl();
    }

    /**
     * Returns the environment in which the current test is running, that is,
     * the set of properties contained in the test context
     * 
     * @return
     */
    public Object getEnvironment(String key) {
        return context.getEnvironment().get(key);
    }

    public void fillProperties(TestProperties callProperties) {
        callProperties.putAll(properties);
    }

    protected abstract TestRunner getTestRunner();

    public void cancel() {
        this.cancelled = true;
    }

    @Override
    protected void runTest() throws Throwable {
        // if the context events have not been initialized by anything else, do
        // so and install a hook so that we can notify test listeners about test
        // end before the test suite dies (supposedly we're being run by an IDE
        // or by a build system such as Ant or Maven)
        if (context.getState() == TestSuiteState.ready) {
            context.fireTestSuiteStarting();
            Runtime.getRuntime().addShutdownHook(new Thread() {
            
                @Override
                public void run() {
                    if(context.getState() == TestSuiteState.running)
                        context.fireTestSuiteEnding();
                }
            
            });
        }
        
        // now run the test as requested
        getTestRunner().runTests();
    }

    protected TestExecutor buildTestExecutor() {
        return new JUnitTestExecutor(AbstractDuckHawkTest.this, getRunMethod());
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
}
