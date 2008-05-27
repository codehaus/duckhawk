package org.duckhawk.core;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

/**
 * A little scaffolding to avoid repeating over and over the same code (but
 * allows for variants)
 * 
 * @author Andrea Aime (TOPP)
 */
public class TestRunnerScaffolding {
    protected TestMetadata metadata;

    protected TestProperties emptyProperties;

    protected TestExecutor baseExecutor;

    protected List<TestExecutor> cloneExecutors = new ArrayList<TestExecutor>();

    /**
     * @uml.property name="listeners"
     * @uml.associationEnd multiplicity="(0 -1)"
     */
    protected TestListener[] listeners;

    protected TestRunner runner;

    protected int expectedTestExecutors;

    protected int expectedRunCount;

    private TestContext context;

    private TestType type;

    public TestRunnerScaffolding(TestType type) {
        this(type, 1, 1);
    }

    public TestRunnerScaffolding(TestType type, int expectedTestExecutors,
            int expectedRunCount) {
        this.type = type;
        this.expectedTestExecutors = expectedTestExecutors;
        this.expectedRunCount = expectedRunCount;
    }

    protected TestRunner buildTestRunner(TestContext context,
            TestExecutor executor) {
        return new ConformanceTestRunner(context, executor);
    }

    public void performTest() throws Throwable {
        init();
        run();
        check();
    }

    private void check() {
        // make sure all expectations are matched
        verify(baseExecutor);
        for (TestExecutor executor : cloneExecutors) {
            verify(executor);
        }
        for (TestListener testListener : listeners) {
            verify(testListener);
        }
    }

    void init() throws Throwable {
        metadata = new TestMetadata("whosGonnaTestTheTests", "0.1", "test",
                type);
        emptyProperties = new TestPropertiesImpl();
        listeners = buildTestListeners();
        context = new DefaultTestContext("whosGonnaTestTheTests", "0.1", emptyProperties, listeners);
        baseExecutor = buildExecutor();

        runner = buildTestRunner(context, baseExecutor);
    }

    protected void run() {
        runner.runTests();
        runner.dispose();
    }

    protected TestListener[] buildTestListeners() {
        // build a listener and set expectations
        TestListener listener = createMock(TestListener.class);
        listener
                .testRunStarting(eq(metadata), isA(TestProperties.class), eq(1));
        listener.testCallExecuted(isA(TestExecutor.class), eq(metadata),
                isA(TestProperties.class), anyDouble(), eq((Throwable) null));
        listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
        replay(listener);
        return new TestListener[] { listener };
    }

    protected TestExecutor buildExecutor() throws Throwable {
        // build an executor that does nothing (and set expectations)
        TestExecutor executor = createMock(TestExecutor.class);
        executor.init(isA(TestProperties.class), isA(TestProperties.class));
        executor.run(isA(TestProperties.class));
        executor.check(isA(TestProperties.class));
        expect(executor.getTestId()).andReturn("test").anyTimes();
        replay(executor);
        return executor;
    }
    
    /**
     * A Matcher used to check a certain set of properties contains at least the expected ones
     * @author Andrea Aime (TOPP)
     *
     */
    class PropertiesMatcher implements IArgumentMatcher {
        TestProperties expected;
        
        public PropertiesMatcher(TestProperties expected) {
            this.expected = expected;
        }
        

        public void appendTo(StringBuffer sb) {
            sb.append("includeProperties(Test properties did not contain the expected values)");
        }

        public boolean matches(Object argument) {
            if(!(argument instanceof TestProperties))
                return false;
            TestProperties props = (TestProperties) argument;
            for (String key : expected.keySet()) {
                if(!props.containsKey(key) || !expected.get(key).equals(props.get(key)))
                    return false;
            }
            return true;
        }
        
    }
    
    /**
     * Matches the argument if it's a {@link TestProperties} and contains at least the specified properties
     * @param expected
     * @return
     */
    TestProperties includeProperties(TestProperties expected) {
        EasyMock.reportMatcher(new PropertiesMatcher(expected));
        return expected;
    }
}