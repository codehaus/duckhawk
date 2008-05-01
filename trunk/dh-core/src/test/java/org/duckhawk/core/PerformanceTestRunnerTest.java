package org.duckhawk.core;
import static org.easymock.EasyMock.*;
import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class PerformanceTestRunnerTest extends TestCase {
    
    public void testBuilExceptions() {
        TestContext context = new TestContext("test", "0.1", new TestPropertiesImpl());
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        try {
            new PerformanceTestRunner(context, executor, -10);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        try {
            new PerformanceTestRunner(context, executor, 0);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        // this one should work
        new PerformanceTestRunner(context, executor, 1);
    }

    public void testRunRepeatedSingleThread() throws Throwable {
        new TestRunnerScaffolding(TestType.performance) {
            private Object thread;
            
            @Override
            protected TestRunner buildTestRunner(TestContext context, TestExecutor executor) {
                return new PerformanceTestRunner(context, executor, 20);
            }

            @Override
            protected TestListener[] buildTestListeners() {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(20));
                listener.testCallExecuted(isA(TestExecutor.class), eq(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().times(20);
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] { listener };
            }
            
            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                // check the thread running this thing is just one
                expectLastCall().andAnswer(new IAnswer<Object>() {
                    public Object answer() throws Throwable {
                        if(thread == null)
                            thread = Thread.currentThread();
                        else
                            assertEquals(thread, Thread.currentThread());
                        return null;
                    }
                
                });
                // check it's called 20 timed (timed runs) + 1 (warmup)
                expectLastCall().times(20 + 1);
                executor.check(emptyProperties);
                expectLastCall().times(20 + 1);
                expect(executor.getTestId()).andReturn("test").anyTimes();
                replay(executor);
                return executor;
            }
        }.performTest();
    }
    
    public void testAccumulateProperties() throws Throwable {
        new TestRunnerScaffolding(TestType.performance) {
            int count;
            
            @Override
            protected TestRunner buildTestRunner(TestContext context, TestExecutor executor) {
                return new PerformanceTestRunner(context, executor, 20);
            }

            @Override
            protected TestListener[] buildTestListeners() {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(20));
                listener.testCallExecuted(isA(TestExecutor.class), eq(metadata),
                        isA(TestProperties.class), anyDouble(), eq((Throwable) null));
                expectLastCall().andAnswer(new IAnswer<Object>() {
                
                    public Object answer() throws Throwable {
                        TestProperties props = (TestProperties) getCurrentArguments()[2];
                        // make sure properties do not accumulate during runs
                        assertEquals("expected proprerty count", 0, props.size());
                        props.put("property" + count, "");
                        count++;
                        return null;
                    }
                
                });
                expectLastCall().times(20);
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] { listener };
            }
            
            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(emptyProperties);
                // check it's called 20 timed (timed runs) + 1 (warmup)
                expectLastCall().times(20 + 1);
                executor.check(emptyProperties);
                expectLastCall().times(20 + 1);
                expect(executor.getTestId()).andReturn("test").anyTimes();
                replay(executor);
                return executor;
            }
        }.performTest();
    }

}
