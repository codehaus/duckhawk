/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.duckhawk.util;

import junit.framework.Assert;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.easymock.EasyMock;

/**
 * A helper class to run the same kind of test against the performance
 * summarizer using different input sequences
 * 
 * @author Andrea Aime (TOPP)
 */
public class PerformanceSummarizerTestScaffolding {
    double[] times;

    private PerformanceSummarizer summarizer;

    private TestMetadata metadata;

    private TestExecutor executor;

    private int callCount;

    private double min;

    private double max;

    private double average;

    private double median;

    private double total;

    private int expectedCallCount;

    public PerformanceSummarizerTestScaffolding(double[] times,
            PerformanceSummarizer summarizer, double min, double max,
            double average, double median, double total) {
        this(times, summarizer, min, max, average, median, total, times.length);
    }

    public PerformanceSummarizerTestScaffolding(double[] times,
            PerformanceSummarizer summarizer, double min, double max,
            double average, double median, double total, int expectedCallCount) {
        this.metadata = new TestMetadata("whosGonnaTestTheTests", "0.1", "test", TestType.undetermined);
        this.executor = EasyMock.createNiceMock(TestExecutor.class);
        this.times = times;
        this.summarizer = summarizer;
        this.expectedCallCount = expectedCallCount;
        this.callCount = times.length;
        this.min = min;
        this.max = max;
        this.average = average;
        this.median = median;
        this.total = total;
    }

    public void runTest() throws AssertionError {
        fillSummarizerManually(times);
        performChecks(null);
        TestProperties props = fillSummarizerEvents(times);
        performChecks(props);
    }

    protected void performChecks(TestProperties properties)
            throws AssertionError {
        Assert.assertEquals(callCount, summarizer.getCallCount());
        Assert.assertEquals(min, summarizer.getMin());
        Assert.assertEquals(max, summarizer.getMax());
        Assert.assertEquals(average, summarizer.getAverage());
        Assert.assertEquals(median, summarizer.getMedian());
        Assert.assertEquals(total, summarizer.getTotal());

        if (properties != null) {
            Assert.assertEquals(Integer.valueOf(callCount), properties
                    .get(TestExecutor.KEY_CALL_COUNT));
            Assert.assertEquals(Double.valueOf(min), properties
                    .get(TestExecutor.KEY_MIN_TIME));
            Assert.assertEquals(Double.valueOf(max), properties
                    .get(TestExecutor.KEY_MAX_TIME));
            Assert.assertEquals(Double.valueOf(average), properties
                    .get(TestExecutor.KEY_AVG_TIME));
            Assert.assertEquals(Double.valueOf(median), properties
                    .get(TestExecutor.KEY_MED_TIME));
            Assert.assertEquals(Double.valueOf(total), properties
                    .get(TestExecutor.KEY_TOTAL_TIME));
        }
    }

    protected void fillSummarizerManually(double[] times) {
        summarizer.start(expectedCallCount);
        for (double time : times) {
            summarizer.accumulate(time);
        }
        summarizer.done();
    }

    protected TestProperties fillSummarizerEvents(double[] times) {
        TestProperties testProperties = new TestPropertiesImpl();
        summarizer.testRunStarting(metadata, testProperties, expectedCallCount);
        for (double time : times) {
            summarizer.testCallExecuted(executor, metadata,
                    new TestPropertiesImpl(), time, null);
        }
        summarizer.testRunCompleted(metadata, testProperties);
        return testProperties;
    }
}
