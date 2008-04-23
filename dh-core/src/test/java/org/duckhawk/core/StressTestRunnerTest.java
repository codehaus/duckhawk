package org.duckhawk.core;

import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.IAnswer;

public class StressTestRunnerTest extends TestCase {

    public void testBuilExceptions() {
        try {
            new StressTestRunner(-10, 10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(0, 10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(10, -10);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        try {
            new StressTestRunner(10, 0);
            fail("This should have failed!");
        } catch (Exception e) {
            // fine, it's what I expect
        }

        // this one should work
        new StressTestRunner(1, 1);
    }

    public void testRunRepeatedMultiThread() throws Throwable {
        final Set<Thread> threads = new HashSet<Thread>();
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
                // check it's called (5 times (timed runs) + 1 (warmup)) * 5
                // threads
                expectLastCall().times((5 + 1) * 5);
                executor.check(emptyProperties);
                expectLastCall().times((5 + 1) * 5);
                replay(executor);
                return executor;
            }

            @Override
            protected TestExecutorFactory buildFactory(TestExecutor executor) {
                // build a factory and set expectations
                TestExecutorFactory factory = createMock(TestExecutorFactory.class);
                expect(factory.createMetadata()).andReturn(metadata);
                expect(factory.createTestExecutor()).andReturn(executor).times(
                        5);
                replay(factory);
                return factory;
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
                        if (thread == null)
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
                replay(executor);
                return executor;
            }
        }.performTest();
    }
    
    public void testRampUp() throws Throwable {
        final Set<Thread> threads = new HashSet<Thread>();
        final long start = System.nanoTime();
        final int numThreads = 50;
        final int rampUp = 2;
        final int requests = 5;
        new TestRunnerScaffolding() {
            @Override
            protected TestRunner buildTestRunner() {
                return new StressTestRunner(requests, numThreads, rampUp);
            }

            @Override
            protected TestListener[] buildTestListeners(TestExecutor executor) {
                TestListener listener = createMock(TestListener.class);
                listener.testRunStarting(metadata, emptyProperties, requests * numThreads);
                // make sure we don't get any exception from the runner, since that means
                // the assert failed
                listener.testCallExecuted(eq(executor), eq(metadata),
                        eq(emptyProperties), anyDouble(), eq((Throwable) null));
                expectLastCall().times(requests * numThreads);
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
                        Thread.sleep(50);
                        // make sure we're actually ramping up, that is, the elapsed time over the number of
                        // different threads seen so far is bigger than the ramp up rate (we cannot test equality,
                        // a well working system will have it slightly bigger)
                        double elapsed = (System.nanoTime() - start) / 1000000.0;
                        assertTrue(elapsed / threads.size() > (rampUp / numThreads)); 
                        return null;
                    }

                });
                // check it's called requests times by each thread, plus one extra warmup call per thread
                expectLastCall().times((requests + 1) * numThreads);
                executor.check(emptyProperties);
                expectLastCall().times((requests + 1) * numThreads);
                replay(executor);
                return executor;
            }

            @Override
            protected TestExecutorFactory buildFactory(TestExecutor executor) {
                // build a factory and set expectations
                TestExecutorFactory factory = createMock(TestExecutorFactory.class);
                expect(factory.createMetadata()).andReturn(metadata);
                expect(factory.createTestExecutor()).andReturn(executor).times(
                        numThreads);
                replay(factory);
                return factory;
            }
        }.performTest();
        assertEquals(threads.size(), numThreads);
    }
}
