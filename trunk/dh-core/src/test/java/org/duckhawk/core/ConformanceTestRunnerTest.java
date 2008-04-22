package org.duckhawk.core;
import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import org.easymock.IAnswer;

public class ConformanceTestRunnerTest extends TestCase {

    
    public void testSuccessfulRun() throws Throwable {
        new TestRunnerScaffolding().performTest();
    }
    
    public void testFailingRun() throws Throwable {
        new TestRunnerScaffolding() {
            Throwable t = new Throwable("I'm the ugly exception");            
        
            @Override
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                // build a listener that will expect throwables
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, emptyProperties, 1);
                listener.testCallExecuted(same(executor), same(metadata),
                        eq(emptyProperties), anyDouble(), eq(t));
                listener.testRunCompleted(metadata, emptyProperties);
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
        new TestRunnerScaffolding() {
            
            protected TestExecutorFactory buildFactory(TestExecutor executor) {
                // build a factory and set expectations
                TestExecutorFactory factory = createMock(TestExecutorFactory.class);
                expect(factory.createMetadata()).andReturn(metadata).times(2);
                expect(factory.createTestExecutor()).andReturn(executor).times(2);
                replay(factory);
                return factory;
            }

            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                expectLastCall().times(2);
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
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                return new TestListener[] {};
            }
        }.performTest();
    }
    
    public void testFaultyFactory() throws Throwable {
        final RuntimeException t = new RuntimeException("I'm the ugly exception");
        try {
            new TestRunnerScaffolding() {
                protected TestExecutorFactory buildFactory(TestExecutor executor) {
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
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                // build a listener that will expect the properties the runner has set
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, emptyProperties, 1);
                listener.testCallExecuted(same(executor), same(metadata),
                        eq(callProperties), anyDouble(), eq((Throwable) null));
                listener.testRunCompleted(metadata, emptyProperties);
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
                replay(executor);
                return executor;
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
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                // build a listener that will add a property
                TestListener listener1 = createMock(TestListener.class);
                listener1.testRunStarting(metadata, emptyProperties, 1);
                listener1.testCallExecuted(same(executor), same(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().andAnswer(new IAnswer<Object>() {

                    public Object answer() throws Throwable {
                        TestProperties props = (TestProperties) getCurrentArguments()[2];
                        props.putAll(listenerProperties);
                        return null;
                    }

                });
                listener1.testRunCompleted(metadata, emptyProperties);
                replay(listener1);

                // build a listener that will check that the property set by the other
                // listener is visible
                TestListener listener2 = createMock(TestListener.class);
                listener2.testRunStarting(metadata, emptyProperties, 1);
                listener2.testCallExecuted(same(executor), same(metadata),
                        eq(listenerProperties), anyDouble(), eq((Throwable) null));
                listener2.testRunCompleted(metadata, emptyProperties);
                replay(listener2);
                
                return new TestListener[] {listener1, listener2};
            }
        }.performTest();
    }

}
