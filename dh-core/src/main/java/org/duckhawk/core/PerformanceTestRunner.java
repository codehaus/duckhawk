package org.duckhawk.core;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Random;

/**
 * Runs a certain test a number of times in sequence, with a warm up run before
 * the actual performance test runs (so the {@link TestExecutor} is actually run
 * <code>repetition + 1</code> times, whilst listeners only get notified
 * <code>repetition</code> times.
 * <p>
 * The same {@link TestExecutor} is used to run all the calls.
 * <p>
 * This can be configured to run all the calls one after the other, or to
 * perform the calls over a certain period of time using a random distribution
 * generator.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class PerformanceTestRunner extends ConformanceTestRunner  {
    /**
     * The number of repetitions for this performance test
     */
    public static final String KEY_REPETITIONS = "test.repetitions";
    
    /**
     * Number of seconds in which the test is run (for randomly distributed tests)
     */
    public static final String KEY_TIME = "test.distributionTime";
    
    /**
     * The distribution generator for the random distributed test
     */
    public static final String KEY_DISTRIBUTION = "test.distributionGenerator";
    
    /**
     * How many times the test must be run (besides the warm up run)
     */
    protected int repetitions;

    protected double time;

    protected Random random;

    /**
     * Builds a performance test runner that will issue <code>repetition</code>
     * requests one after the other. Before running the
     * <code>repetitions<code> requests a warm up call will be made. 
     * @param repetitions
     */
    public PerformanceTestRunner(int repetitions) {
        this.repetitions = repetitions;
        ensurePositive(repetitions, "repetitions", true);
    }

    /**
     * Builds a performance test runner that will issue <code>repetition</code>
     * requests in the given time, using a random distribution generated with
     * the specified Random provider (libraries such as
     * {@link http://uncommons-maths.dev.java.net/ uncommons-math} do provide
     * Random subclasses with specific distributions other than the normal one
     * provided by Random).
     * <p>
     * Before running the
     * <code>repetitions<code> requests a warm up call will be made. 
     * @param repetitions Number of {@link TestExecutor} calls to be made
     * @param time Total time in seconds
     * @param random The random generator
     */
    public PerformanceTestRunner(int repetitions, double time, Random random) {
        this.repetitions = repetitions;
        this.time = time;
        this.random = random;
        ensurePositive(repetitions, "repetitions", true);
        ensurePositive(time, "time", true);
        if (random == null)
            throw new InvalidParameterException(
                    "Parameter 'random' cannot be null");
    }

    /**
     * Checks the specified number is positive
     * 
     * @param number
     * @param variable
     * @param strict
     */
    protected void ensurePositive(double number, String variable, boolean strict) {
        if (number < 0 || (strict && number == 0))
            throw new InvalidParameterException("Parameter " + variable
                    + " must be positive");
    }

    @Override
    public void runTests(TestExecutorFactory factory) {
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        testProperties.put(TestExecutor.KEY_TEST_TYPE, TestType.performance.toString());
        testProperties.put(KEY_REPETITIONS, TestType.performance.toString());
        if(random != null) {
            testProperties.put(KEY_TIME, time);
            testProperties.put(KEY_DISTRIBUTION, random);
        }
        fireStartEvent(metadata, testProperties, repetitions);
        try {
            TestExecutor executor = factory.createTestExecutor();
            runLinear(executor, metadata);
        } finally {
            fireEndEvent(metadata, testProperties);
        }
    }

    /**
     * Runs the TestExecutor in a linear fashion. Depending on how this class is
     * configured, a repeated or distributed delay run will happen
     * 
     * @param executor
     * @param metadata
     */
    protected void runLinear(TestExecutor executor, TestMetadata metadata) {
        if (random != null)
            runDistributedDelay(executor, metadata);
        else
            runRepeated(executor, metadata);
    }

    protected void runDistributedDelay(TestExecutor executor,
            TestMetadata metadata) {
        warmup(executor);

        // generate the random times distribution (in nanoseconds, since we'll
        // use
        // System.nanoTime() to control the advancement
        long[] targets = new long[repetitions];
        for (int i = 0; i < targets.length; i++) {
            targets[i] = (long) (random.nextDouble() * time * 10e9);
        }
        // sort them so that we can use them in a wait and run loop
        Arrays.sort(targets);

        // mark start time and turn all delays into targets
        long start = System.nanoTime();
        for (int i = 0; i < targets.length; i++) {
            targets[i] += start;
        }

        // make the calls
        TestProperties runProperties = new TestPropertiesImpl();
        for (int i = 0; i < repetitions; i++) {
            runProperties.clear();
            sleepUpToTarget(start, targets[i]);
            runSingle(executor, metadata, runProperties);
        }
    }

    /**
     * Makes this thread wait up to the specified target time
     * 
     * @param start
     * @param targetTime
     */
    protected void sleepUpToTarget(long start, long targetTime) {
        long sleepTime = targetTime - (System.nanoTime() - start) / 1000000;
        while (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // forget about it and go back on sleeping if necessary
            }
            sleepTime = targetTime - (System.nanoTime() - start) / 1000000;
        }
    }

    /**
     * Warms up the test once and then repeats the test over and over
     * 
     * @param executor
     * @param metadata
     */
    protected void runRepeated(TestExecutor executor, TestMetadata metadata) {
        warmup(executor);

        // loop and time
        TestProperties runProperties = new TestPropertiesImpl();
        for (int i = 0; i < repetitions; i++) {
            // clean up properties and run test
            runProperties.clear();
            runSingle(executor, metadata, runProperties);
        }
    }

    private void warmup(TestExecutor executor) {
        TestProperties warmupProperties = new TestPropertiesImpl();
        try {
            executor.run(warmupProperties);
            executor.check(warmupProperties);
        } catch (Throwable t) {
            // this was just a warmup, we don't notify the listeners
        }
    }
}
