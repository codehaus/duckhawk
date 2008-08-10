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

package org.duckhawk.core;

import java.util.ArrayList;
import java.util.List;

public class StoppableTestExecutor implements TestExecutor {

    boolean canceled;

    boolean checkPerformed;

    boolean timedOut;

    List<StoppableTestExecutor> clonedExecutors = new ArrayList<StoppableTestExecutor>();

    public void cancel() throws Throwable {
        this.canceled = true;
    }

    public void check(TestProperties callProperties) throws Throwable {
        this.checkPerformed = true;
    }

    public void run(TestProperties callProperties) throws Throwable {
        long start = System.currentTimeMillis();
        while (true) {
            // sleep a very little while
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                System.out.println("Stoppable test executor: " + e);
            }

            // check if cancelled
            if (canceled)
                break;

            // make sure we don't stay here forever, the test should
            // not take more than 30 seconds
            if ((System.currentTimeMillis() - start) > 30000)
                timedOut = true;
        }
    }

    public TestExecutor cloneExecutor() {
        StoppableTestExecutor executor = new StoppableTestExecutor();
        clonedExecutors.add(executor);
        return executor;
    }

    public String getTestId() {
        return "ThisIsTheStoppableTest";
    }

    public void init(TestProperties enviroment, TestProperties testProperties) {
        // nothing to do here
    }
}
