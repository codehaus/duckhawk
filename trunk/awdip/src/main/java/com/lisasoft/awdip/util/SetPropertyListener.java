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

package com.lisasoft.awdip.util;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestSuiteListener;

public class SetPropertyListener implements TestSuiteListener {
    /** Properties for a single test call that should be at least initialized
     * with "null" */ 
    String[] forcePropertiesOutput;

    
    public SetPropertyListener(String[] forcePropertiesOutput) {
        this.forcePropertiesOutput = forcePropertiesOutput;
    }

    public void testSuiteCompleted(TestContext context) {
    }

    public void testSuiteStarting(TestContext context) {
    }

    /**
     * Set not initialized keys after test call was executed
     */
    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
        for (String key : forcePropertiesOutput) {
            if (!callProperties.containsKey(key))
                callProperties.put(key, null);
        }
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
    }

}
