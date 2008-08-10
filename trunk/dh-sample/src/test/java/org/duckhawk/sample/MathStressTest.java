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

package org.duckhawk.sample;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.StressTest;

public class MathStressTest extends StressTest {

    public MathStressTest() {
        // Load test, 5 threads in parallel each doing 10 requests, with a ramp
        // up time of 3 seconds
        super(TestSupport.getContext(), 10, 5, 2);
    }

    public void testSqrt() throws Exception {
        // make this test blow up 10% of the times
        if(Math.random() < 0.1)
            throw new Exception("This is a random failure in testSqrt");
        
        putCallProperty(TestExecutor.KEY_REQUEST, "sqrt(12.5)");
        double result = Math.sqrt(12.5);
        Thread.sleep(50);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testSin() throws Exception {
        putCallProperty(TestExecutor.KEY_REQUEST, "sin(12.5)");
        double result = Math.sin(12.5);
        Thread.sleep(50);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testLog() throws Exception {
        putCallProperty(TestExecutor.KEY_REQUEST, "log(12.5)");
        double result = Math.log(12.5);
        Thread.sleep(50);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
}
