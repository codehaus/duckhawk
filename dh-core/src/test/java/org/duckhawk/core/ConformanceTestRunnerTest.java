package org.duckhawk.core;
import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import org.easymock.IAnswer;

public class ConformanceTestRunnerTest extends TestCase {
    
    private TestPropertiesImpl testProperties;

    

    
    public void testSuccessfulRun() throws Throwable {
        new TestRunnerScaffolding().performTest();
    }
    
    public void testFailingRun() throws Throwable {
        new TestRunnerScaffolding() {
            Throwable t = new Throwable("I'm the ugly exception");            
        
            @Override
            protected TestListener[] buildTestListeners() {
                // build a listener that will expect throwables
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(1));
                listener.testCallExecuted(isA(TestExecutor.class), same(metadata),
                        eq(emptyProperties), anyDouble(), eq(t));
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] {listener};
            }
        
            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does throw an exception (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                expectLastCall().andThrow(t);
                replay(executor);
                return executor;
            }
        
        }.performTest();
    }
    
    public void testRemoveListeners() throws Throwable {
        new TestRunnerScaffolding(1, 2) {
            
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                executor.check(emptyProperties);
                replay(executor);
                return executor;
            }
        
            protected void run() {
                // run the tests
                ConformanceTestRunner runner = new ConformanceTestRunner();
                for (TestListener testListener : listeners) {
                    runner.addTestRunListener(testListener);
                }
                runner.runTests(factory);
                
                // remove the listeners and run again
                for (TestListener testListener : listeners) {
                    runner.removeTestRunListener(testListener);
                }
                runner.runTests(factory);
            }
        
        }.performTest();
    }

    /**
     * Make sure it does not break even without listeners
     * @throws Throwable
     */
    public void testNoListeners() throws Throwable {
        new TestRunnerScaffolding() {
            @Override
            protected TestListener[] buildTestListeners() {
                return new TestListener[] {};
            }
        }.performTest();
    }
    
    public void testFaultyFactory() throws Throwable {
        final RuntimeException t = new RuntimeException("I'm the ugly exception");
        try {
            new TestRunnerScaffolding() {
                @Override
                protected TestExecutorFactory buildFactory(int numThreads, int numExpectedRun) {
                    // build a factory and set expectations
                    TestExecutorFactory factory = createMock(TestExecutorFactory.class);
                    expect(factory.createMetadata()).andReturn(metadata);
                    expect(factory.createTestExecutor()).andThrow(t);
                    replay(factory);
                    return factory;
                };
            }.performTest();
            fail("This should have thrown an exception!");
        } catch(Throwable tActual) {
            assertSame(t, tActual);
        }
    }

    public void testRunnerProperties() throws Throwable {
        new TestRunnerScaffolding() {
            TestProperties callProperties = new TestPropertiesImpl();
            
            {
                callProperties.put("test", "test runner was here!");
            }
        
            @Override
            protected TestListener[] buildTestListeners() {
                // build a listener that will expect the properties the runner has set
                TestListener listener = createNiceMock(TestListener.class);
                listener.testCallExecuted(isA(TestExecutor.class), same(metadata),
                        eq(callProperties), anyDouble(), eq((Throwable) null));
                replay(listener);
                return new TestListener[] {listener};
            }
        
            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does throw an exception (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                expectLastCall().andAnswer(new IAnswer<Object>() {

                    public Object answer() throws Throwable {
                        TestProperties props = (TestProperties) getCurrentArguments()[0];
                        props.putAll(callProperties);
                        return null;
                    }

                });
                executor.check(callProperties);
                replay(executor);
                return executor;
            }
        
        }.performTest();
    }
    
    public void testTestProperties() throws Throwable {
        new TestRunnerScaffolding() {
            TestProperties testProperties = new TestPropertiesImpl();
            
            {
                testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.conformance.toString());
            }
        
            @Override
            protected TestListener[] buildTestListeners() {
                // build a listener that will expect the properties the runner has set
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, testProperties, 1);
                listener.testCallExecuted(isA(TestExecutor.class), same(metadata),
                        isA(TestProperties.class), anyDouble(), eq((Throwable) null));
                listener.testRunCompleted(metadata, testProperties);
                replay(listener);
                return new TestListener[] {listener};
            }
        
        }.performTest();
    }

    public void testListenerProperties() throws Throwable {
        new TestRunnerScaffolding() {
            TestProperties listenerProperties = new TestPropertiesImpl();
            
            {
                listenerProperties.put("test", "listener 1 was here!");
            }
        
            @Override
            protected TestListener[] buildTestListeners() {
                // build a listener that will add a property
                TestListener listener1 = createMock(TestListener.class);
                listener1.testRunStarting(eq(metadata), isA(TestProperties.class), eq(1));
                listener1.testCallExecuted(isA(TestExecutor.class), same(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().andAnswer(new IAnswer<Object>() {

                    public Object answer() throws Throwable {
                        TestProperties props = (TestProperties) getCurrentArguments()[2];
                        props.putAll(listenerProperties);
                        return null;
                    }

                });
                listener1.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener1);

                // build a listener that will check that the property set by the other
                // listener is visible
                TestListener listener2 = createMock(TestListener.class);
                listener2.testRunStarting(eq(metadata), isA(TestProperties.class), eq(1));
                listener2.testCallExecuted(isA(TestExecutor.class), same(metadata),
                        eq(listenerProperties), anyDouble(), eq((Throwable) null));
                listener2.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener2);
                
                return new TestListener[] {listener1, listener2};
            }
        }.performTest();
    }

}
