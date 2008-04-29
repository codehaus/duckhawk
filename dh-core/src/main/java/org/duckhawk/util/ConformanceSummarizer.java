package org.duckhawk.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;

/**
 * {@link TestListener} and helper that allows to summarize the exceptions
 * occurred during a test run. Will compute total number of calls, number of
 * failures and provide a summary of the error messages.
 */
public class ConformanceSummarizer implements TestListener {

    Set<String> exceptionMessages = new HashSet<String>();

    int errorCount;

    int callCount;

    ExceptionConverter converter = new ExceptionConverter();

    /**
     * Prepares the summarizer for a summarization run (zeroes the counters and
     * the accumulated exception set).
     */
    public void start() {
        errorCount = 0;
        callCount = 0;
        exceptionMessages.clear();
    }

    /**
     * Records a result (exception or lack of thereof).
     * 
     * @param exception
     *                The exception
     */
    public void accumulate(Throwable exception) {
        callCount++;
        if (exception != null) {
            errorCount++;
            exceptionMessages.add(converter.convert(exception));
        }

    }

    /**
     * Returns a set of all the unique error messages collected during the run
     * 
     * @return
     */
    public Set<String> getUniqueExceptionMessages() {
        return Collections.unmodifiableSet(exceptionMessages);
    }

    /**
     * Returns the number of errors accumulated so far in the test run
     * 
     * @return
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Returns the total number of calls to {@link #accumulate(Throwable)}
     * 
     * @return
     */
    public int getCallCount() {
        return callCount;
    }

    /**
     * Returns the percentage of errors over the total number of calls (as a
     * double between 0 and 1)
     * 
     * @return
     */
    public double getErrorPercentage() {
        if (callCount == 0)
            return 0d;
        else
            return errorCount * 1.0 / callCount;
    }

    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
        accumulate(exception);
    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
        start();
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
        testProperties.put(TestExecutor.KEY_ERROR_SUMMARY,
                getUniqueExceptionMessages());
        testProperties.put(TestExecutor.KEY_ERROR_COUNT, getErrorCount());
        testProperties.put(TestExecutor.KEY_CALL_COUNT, getCallCount());
        testProperties.put(TestExecutor.KEY_ERROR_PERCENTAGE,
                getErrorPercentage());
    }

}
