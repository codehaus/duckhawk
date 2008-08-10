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

package org.duckhawk.junit3;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestRunner;

/**
 * @author Andrea Aime (TOPP)
 */
public abstract class StressTest extends AbstractDuckHawkTest {

    protected int times;

    private int numThreads;

    private int rampUp;

    public StressTest(TestContext context, int times, int numThreads, int rampUp) {
        super(context);
        this.times = times;
        this.numThreads = numThreads;
        this.rampUp = rampUp;
    }

    protected TestRunner getTestRunner(TestContext context) {
        return new StressTestRunner(context, buildTestExecutor(), times,
                numThreads, rampUp);
    }

}
