package org.duckhawk.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;

/**
 * A performance numbers summarizer. It can be used direcly or act as a {@link TestListener} . <p> When used directly: <ul> <li>create the summarizer</li> <li>call  {@link #start(int)} </li> <li>call  {@link #accumulate(double)}  for each time event you want to record</li> <li>call  {@link #done()}  when the series end to have the statistics computed</li> <li>call the getters to grab the statistics</li> </ul> </p> <p> When used as a listener: <ul> <li>create the summarizer and put it in the listener chain before any other listener that might use the summaries <li>other listeners will be able to grab the following properties once the test run is complete: <ul> <li> {@link TestExecutor#KEY_CALL_COUNT} </li> <li> {@link TestExecutor#KEY_AVG_TIME} </li> <li> {@link TestExecutor#KEY_MED_TIME} </li> <li> {@link TestExecutor#KEY_MIN_TIME} </li> <li> {@link TestExecutor#KEY_MAX_TIME} </li> <li> {@link TestExecutor#KEY_TOTAL_TIME} </li> </ul> </ul>
 * @author  Andrea Aime (TOPP)
 */
public class PerformanceSummarizer implements TestListener {
    private static NumberFormat format = new DecimalFormat("0.#######");

    /**
     * @uml.property  name="min"
     */
    double min;

    /**
     * @uml.property  name="max"
     */
    double max;

    /**
     * @uml.property  name="total"
     */
    double total;

    /**
     * @uml.property  name="callCount"
     */
    int callCount;

    /**
     * @uml.property  name="average"
     */
    double average;

    double[] times;

    /**
     * @uml.property  name="median"
     */
    double median;

    public void start(int expectedCallCount) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        total = 0;
        callCount = 0;
        average = 0;
        if (expectedCallCount >= 0)
            times = new double[expectedCallCount];
        else
            times = new double[50];
    }

    public void accumulate(double time) {
        if (callCount >= times.length) {
            double[] temp = new double[times.length * 3 / 2 + 1];
            System.arraycopy(times, 0, temp, 0, times.length);
            times = temp;
        }

        times[callCount] = time;
        callCount++;
        total += time;
        if (time < min)
            min = time;
        if (time > max)
            max = time;
    }

    public void done() {
        if(callCount > 0) {
            Arrays.sort(times, 0, callCount);
            if (callCount % 2 == 0) {
                median = (times[(callCount - 1) / 2] + times[callCount / 2]) / 2;
            } else {
                median = times[callCount / 2];
            }
            times = null;
        } else {
            median = Double.NaN;
        }
    }

    /**
     * @return
     * @uml.property  name="total"
     */
    public double getTotal() {
        return total;
    }

    /**
     * @return
     * @uml.property  name="min"
     */
    public double getMin() {
        return min;
    }

    /**
     * @return
     * @uml.property  name="max"
     */
    public double getMax() {
        return max;
    }

    /**
     * @return
     * @uml.property  name="callCount"
     */
    public int getCallCount() {
        return callCount;
    }

    /**
     * @return
     * @uml.property  name="average"
     */
    public double getAverage() {
        return total / callCount;
    }

    /**
     * @return
     * @uml.property  name="median"
     */
    public double getMedian() {
        return median;
    }

    @Override
    public String toString() {
        return "Call count: " + callCount + ", Avg: "
                + format.format(getAverage()) + ", Med: "
                + format.format(median) + ", Min time: " + format.format(min)
                + ", Max time: " + format.format(max);
    }

    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
        accumulate(time);
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
        // compute the summaries
        done();
        // put all the summaries in the test properties
        testProperties.put(TestExecutor.KEY_MIN_TIME, getMin());
        testProperties.put(TestExecutor.KEY_MAX_TIME, getMax());
        testProperties.put(TestExecutor.KEY_AVG_TIME, getAverage());
        testProperties.put(TestExecutor.KEY_MED_TIME, getMedian());
        testProperties.put(TestExecutor.KEY_TOTAL_TIME, getTotal());
        testProperties.put(TestExecutor.KEY_CALL_COUNT, getCallCount());
    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
        start(callNumber);
    }
    
    public void testSuiteCompleted() {
        // nothing to do here, this listener does not need to close up anything 
    }
}
