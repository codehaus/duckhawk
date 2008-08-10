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


import junit.textui.TestRunner;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.PerformanceTest;

public class MathPerfTest extends PerformanceTest {
    
    double result;
    
    @Override
    protected void setUp() throws Exception {
        // Keep this empty method here, a bug occurred that was triggered when setUp() did not
        // call back the super one
    }

    public MathPerfTest() {
        super(TestSupport.getContext(), 50);
    }

    public void initSqrt(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION, "This test just extracts the square root of 12.5, " +
        		"but it's designed to fail in the check phase");
    }
    
    public void testSqrt() {
        putCallProperty(TestExecutor.KEY_REQUEST, "sqrt(12.5)");
        result = Math.sqrt(4);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
    
    public void checkSqrt() {
        // make an assertion that will fail
        assertEquals(4.0, result);
    }

    public void testSin() {
        putCallProperty(TestExecutor.KEY_REQUEST, "sin(12.5)");
        result = Math.sin(12.5);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }

    public void testLog() {
        putCallProperty(TestExecutor.KEY_REQUEST, "log(12.5)");
        result = Math.log(12.5);
        putCallProperty(TestExecutor.KEY_RESPONSE, Double.valueOf(result));
    }
    
    public static void main(String[] args) {
        TestRunner.run(MathPerfTest.class);
    }

}
