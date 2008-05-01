package org.duckhawk.core;

import static org.easymock.EasyMock.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class StressTestRunnerTest extends TestCase {

    public void testBuilExceptions() {
        TestContext context = new TestContext("test", "0.1", new TestPropertiesImpl());
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        try {
            new StressTestRunner(context, executor, -10, 10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(context, executor, 0, 10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(context, executor, 10, -10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(context, executor, 10, 0);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        // this one should work
        new StressTestRunner(context, executor, 1, 1);
    }
    
    public void testRunRepeatedMultiThread() throws Throwable {
        final Set<Thread> threads = Collections
                .synchronizedSet(new HashSet<Thread>());
        new TestRunnerScaffolding(TestType.stress, 5, 1) {
            @Override
            protected TestRunner buildTestRunner(TestContext context, TestExecutor executor) {
                return new StressTestRunner(context, executor, 5, 5);
            }

            @Override
            protected TestListener[] buildTestListeners() {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(25));
                listener.testCallExecuted(isA(TestExecutor.class),
                        eq(metadata), isA(TestProperties.class), anyDouble(),
                        eq((Throwable) null));
                expectLastCall().times(25);
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] { listener };
            }

            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                expect(executor.cloneExecutor()).andAnswer(new IAnswer<TestExecutor>() {
                
                    public TestExecutor answer() throws Throwable {
                        return buildClonedExecutor();
                    }
                
                }).times(5);
                expect(executor.getTestId()).andReturn("test").anyTimes();
                replay(executor);
                return executor;
            }
            
            protected TestExecutor buildClonedExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(isA(TestProperties.class));
                // check it's called 5 timed (timed runs) + 1 (warmup)
                expectLastCall().times(5 + 1);
                executor.check(isA(TestProperties.class));
                expectLastCall().times(5 + 1);
                replay(executor);
                
                cloneExecutors.add(executor);
                
                return executor;
            }
        }.performTest();
    }

    /**
     * Stress testing actually fall back on standard perf testing with a single
     * thread. Make sure it works as expected
     * 
     * @throws Throwable
     */
    public void testRunRepeatedSingleThread() throws Throwable {
        new TestRunnerScaffolding(TestType.stress) {
            private Object thread;

            @Override
            protected TestRunner buildTestRunner(TestContext context, TestExecutor executor) {
                return new StressTestRunner(context, executor, 20, 1);
            }

            @Override
            protected TestListener[] buildTestListeners() {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(20));
                listener.testCallExecuted(isA(TestExecutor.class),
                        eq(metadata), isA(TestProperties.class), anyDouble(),
                        eq((Throwable) null));
                expectLastCall().times(20);
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] { listener };
            }

            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(isA(TestProperties.class));
                // check the thread running this thing is just one
                expectLastCall().andAnswer(new IAnswer<Object>() {
                    public Object answer() throws Throwable {
                        if (thread == null)
                            thread = Thread.currentThread();
                        else
                            assertEquals(thread, Thread.currentThread());
                        return null;
                    }

                });
                // check it's called 20 timed (timed runs) + 1 (warmup)
                expectLastCall().times(20 + 1);
                executor.check(isA(TestProperties.class));
                expectLastCall().times(20 + 1);
                expect(executor.getTestId()).andReturn("test").anyTimes();
                replay(executor);
                return executor;
            }
            
        }.performTest();
    }

    public void testRampUp() throws Throwable {
        final Set<Thread> threads = new HashSet<Thread>();
        final long start = System.nanoTime();
        final int numThreads = 10;
        final int rampUp = 2;
        final int requests = 5;
        new TestRunnerScaffolding(TestType.stress, numThreads, 1) {
            @Override
            protected TestRunner buildTestRunner(TestContext context, TestExecutor executor) {
                return new StressTestRunner(context, executor, requests, numThreads, rampUp);
            }

            @Override
            protected TestListener[] buildTestListeners() {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(eq(metadata), isA(TestProperties.class), eq(requests
                        * numThreads));
                // make sure we don't get any exception from the runner, since
                // that means
                // the assert failed
                listener.testCallExecuted(isA(TestExecutor.class),
                        eq(metadata), isA(TestProperties.class), anyDouble(),
                        eq((Throwable) null));
                expectLastCall().times(requests * numThreads);
                listener.testRunCompleted(eq(metadata), isA(TestProperties.class));
                replay(listener);
                return new TestListener[] { listener };
            }
            
            @Override
            protected TestExecutor buildExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                expect(executor.cloneExecutor()).andAnswer(new IAnswer<TestExecutor>() {
                
                    public TestExecutor answer() throws Throwable {
                        return buildClonedExecutor();
                    }
                
                }).times(numThreads);
                expect(executor.getTestId()).andReturn("test").anyTimes();
                replay(executor);
                return executor;
            }
            
            
            protected TestExecutor buildClonedExecutor() throws Throwable {
                // build an executor that does nothing (and set expectations)
                TestExecutor executor = createMock(TestExecutor.class);
                executor.run(isA(TestProperties.class));
                // check the thread running this thing is just one
                expectLastCall().andAnswer(new IAnswer<Object>() {
                    public Object answer() throws Throwable {
                        threads.add(Thread.currentThread());
                        Thread.sleep(50);
                        // make sure we're actually ramping up, that is, the
                        // elapsed time over the number of
                        // different threads seen so far is bigger than the ramp
                        // up rate (we cannot test equality,
                        // a well working system will have it slightly bigger)
                        double elapsed = (System.nanoTime() - start) / 1000000.0;
                        assertTrue(elapsed / threads.size() > (rampUp / numThreads));
                        return null;
                    }

                });
                // check it's called requests times , plus one extra warm up call 
                expectLastCall().times(requests + 1);
                executor.check(isA(TestProperties.class));
                expectLastCall().times(requests + 1);
                replay(executor);
                
                cloneExecutors.add(executor);
                
                return executor;
            }
        }.performTest();
        assertEquals(threads.size(), numThreads);
    }
}
