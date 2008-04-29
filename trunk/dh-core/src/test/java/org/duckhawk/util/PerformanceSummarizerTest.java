package org.duckhawk.util;

import junit.framework.TestCase;

/**
 * @author   Andrea Aime (TOPP)
 */
public class PerformanceSummarizerTest extends TestCase {
    private PerformanceSummarizer summarizer;

    @Override
    protected void setUp() throws Exception {
        summarizer = new PerformanceSummarizer();
    }

    public void testNoTimes() {
        new PerformanceSummarizerTestScaffolding(new double[0], summarizer,
                Double.MAX_VALUE, Double.MIN_VALUE, Double.NaN, Double.NaN, 0.0)
                .runTest();
    }

    public void testOneTime() {
        new PerformanceSummarizerTestScaffolding(new double[] { 152.0 }, summarizer,
                152.0, 152.0, 152.0, 152.0, 152.0).runTest();
    }

    public void testTwoTimes() {
        new PerformanceSummarizerTestScaffolding(new double[] { 1, 3 }, summarizer, 1.0,
                3.0, 2.0, 2.0, 4.0).runTest();
    }

    public void testThreeTimes() {
        new PerformanceSummarizerTestScaffolding(new double[] { 1, 6, 4 }, summarizer,
                1.0, 6.0, 11.0 / 3.0, 4.0, 11.0).runTest();
    }

    /**
     * Checks summarizer still works even when fed with a wrong expected size
     */
    public void testExpandArray() {
        new PerformanceSummarizerTestScaffolding(new double[] { 1, 6, 4 }, summarizer,
                1.0, 6.0, 11.0 / 3.0, 4.0, 11.0, 1).runTest();
    }

    /**
     * Checks summarizer still works even when fed with a wrong expected size
     * (zero, in this case)
     */
    public void testExpandZeroSize() {
        new PerformanceSummarizerTestScaffolding(new double[] { 1, 6, 4 }, summarizer,
                1.0, 6.0, 11.0 / 3.0, 4.0, 11.0, 0).runTest();
    }
    
    /**
     * Checks summarizer still works even when fed with a wrong expected size
     * (negative, in this case)
     */
    public void testExpandNegativeSize() {
        new PerformanceSummarizerTestScaffolding(new double[] { 1, 6, 4 }, summarizer,
                1.0, 6.0, 11.0 / 3.0, 4.0, 11.0, -10).runTest();
    }
}
