package org.duckhawk.core;
import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.duckhawk.core.PerformanceTestRunner;
import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestExecutorFactory;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestRunner;
import org.easymock.IAnswer;


public class StressTestRunnerTest extends TestCase {
   
    public void testBuilExceptions() {
        try {
            new StressTestRunner(-10, 10);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        try {
            new StressTestRunner(0, 10);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        try {
            new StressTestRunner(10, -10);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        try {
            new StressTestRunner(10, 0);
            fail("This should have failed!");
        } catch(Exception e) {
            // fine, it's what I expect
        }
        
        // this one should work
        new StressTestRunner(1, 1);
    }
    
    public void testRunRepeatedMultiThread() throws Throwable {
        final Set threads = new HashSet();
        new TestRunnerScaffolding() {
            @Override
            protected TestRunner buildTestRunner() {
                return new StressTestRunner(5, 5);
            }

            @Override
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, emptyProperties, 25);
                listener.testCallExecuted(eq(executor), eq(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().times(25);
                listener.testRunCompleted(metadata, emptyProperties);
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
                        threads.add(Thread.currentThread());
                        return null;
                    }
                
                });
                // check it's called (5 times (timed runs) + 1 (warmup)) * 5 threads
                expectLastCall().times((5 + 1) * 5);
                replay(executor);
                return executor;
            }
            
            @Override
            protected TestExecutorFactory buildFactory(TestExecutor executor) {
                // build a factory and set expectations
                TestExecutorFactory factory = createMock(TestExecutorFactory.class);
                expect(factory.createMetadata()).andReturn(metadata);
                expect(factory.createTestExecutor()).andReturn(executor).times(5);
                replay(factory);
                return factory;
            }
        }.performTest();
    }
    
    /**
     * Stress testing actually fall back on standard perf testing with a single thread. Make sure it works as expected
     * @throws Throwable
     */
    public void testRunRepeatedSingleThread() throws Throwable {
        new TestRunnerScaffolding() {
            private Object thread;
            
            @Override
            protected TestRunner buildTestRunner() {
                return new StressTestRunner(20, 1);
            }

            @Override
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, emptyProperties, 20);
                listener.testCallExecuted(eq(executor), eq(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().times(20);
                listener.testRunCompleted(metadata, emptyProperties);
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
                replay(executor);
                return executor;
            }
        }.performTest();
    }
}
