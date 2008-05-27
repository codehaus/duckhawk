package org.duckhawk.junit3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.DefaultTestContext.TestSuiteState;

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
     * The test context in which we're running (to be used only in order to
     * create a runner)
     */
    private ExceptionContextWrapper context;

    /**
     * The properties for the single test run.
     */
    private TestProperties properties;

    /**
     * The properties we got during the init phase (which should be the same as
     * the ones in the context, but you never know what a custom runner might
     * do)
     */
    private TestProperties enviroment;

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
        this.context = new ExceptionContextWrapper(context);
        this.enviroment = context.getEnvironment();
        this.properties = new TestPropertiesImpl();
    }
    
    protected abstract TestRunner getTestRunner(TestContext context);

    // //////////////////////////////////////////////////////////////////////////
    // Execution management methods
    // //////////////////////////////////////////////////////////////////////////

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
                    if (context.getState() == TestSuiteState.running)
                        context.fireTestSuiteEnding();
                }

            });
        }

        // now run the test as requested
        getTestRunner(context).runTests();

        // if at least one exception was thrown during the execution of the
        // method, throw it back to make it evident for the JUnit test runners
        // that something wend bad
        Throwable firstException = context.getFirstException();
        if (firstException != null) {
            int exceptionCount = context.getExceptionCount();
            if (exceptionCount == 1)
                throw firstException;
            else
                throw new Exception(exceptionCount
                        + " exceptions occurred during "
                        + "test execution, here is the first one",
                        firstException);
        }

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

    // //////////////////////////////////////////////////////////////////////////
    // Execution management methods
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Returns the environment in which the current test is running, that is,
     * the set of properties contained in the test context
     * 
     * @return
     */
    public Object getEnvironment(String key) {
        return enviroment.get(key);
    }

    /**
     * Allows to store a environment property after initialization. For example
     * to set the description of a test.
     * 
     * @return
     */
    public Object putEnvironment(String key, Object value) {
        return enviroment.put(key, value);
    }

    /**
     * Allows to store a per call property so that listeners will see it in the
     * single call execution event
     * 
     * @param key
     * @param value
     */
    public void putCallProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Returns a the property of the current call.
     * 
     * @param key
     */
    public Object getCallProperty(String key) {
        return properties.get(key);
    }

    /**
     * Returns the whole property object of the current call.
     * 
     * @param key
     */
    public Object getCallPropertyObject() {
        return properties;
    }

    /**
     * Used by the JUnit3 integration to provide back the call properties
     * collected by the test during its execution. Tests should not use it, if
     * you need to add a property to the call
     */
    public void fillCallProperties(TestProperties callProperties) {
        callProperties.putAll(properties);
    }

    public void initEnviroment(TestProperties environment) {
        this.enviroment = environment;
    }
}
