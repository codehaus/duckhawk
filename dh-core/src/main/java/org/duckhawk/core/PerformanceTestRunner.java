package org.duckhawk.core;

import java.security.InvalidParameterException;

/**
 * Runs a certain test a number of times in sequence, without pauses, with a
 * warmup run before the actual performance test runs (so the
 * {@link TestExecutor} is actually run <code>repetition + 1</code> times,
 * whilst listeners only get notified <code>repetition</code> times.<br>
 * The same {@link TestExecutor} is used to run all the calls.
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class PerformanceTestRunner extends ConformanceTestRunner implements
        TestRunner {
    /**
     * How many times the test must be run (besides the warm up run)
     */
    int repetitions;

    public PerformanceTestRunner(int repetitions) {
        this.repetitions = repetitions;
        ensurePositive(repetitions, "repetitions", true);
    }

    protected void ensurePositive(long number, String variable, boolean strict) {
        if (number < 0 || (strict && number == 0))
            throw new InvalidParameterException("Parameter " + variable
                    + " must be positive");
    }

    public void runTests(TestExecutorFactory factory) {
        TestMetadata metadata = factory.createMetadata();
        TestProperties testProperties = new TestPropertiesImpl();
        fireStartEvent(metadata, testProperties, repetitions);
        try {
            TestExecutor executor = factory.createTestExecutor();
            runRepeated(executor, metadata);
        } finally {
            fireEndEvent(metadata, testProperties);
        }
    }

    protected void runRepeated(TestExecutor executor, TestMetadata metadata) {
        // warmup
        TestProperties runProperties = new TestPropertiesImpl();
        try {
            executor.run(runProperties);
        } catch (Throwable t) {
            // notify
        }

        // loop and time
        for (int i = 0; i < repetitions; i++) {
            // clean up properties and run test
            runProperties.clear();
            runSingle(executor, metadata, runProperties);
        }
    }
}
