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
