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

import java.io.File;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.DefaultTestContext;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class TestSupport {
    static TestContext context;

    public static TestContext getContext() {
        if (context == null) {
            TestProperties env = new TestPropertiesImpl();
            env.put("description", "This is a fictous test to show up "
                    + "conformance, performance and stress testing abilities "
                    + "of DuckHawk with the JUnit3 integration");
            context = new DefaultTestContext("Math", "1.0", env,
                    new PerformanceSummarizer(), //
                    new ConformanceSummarizer(), //
                    new PrintStreamListener(false, true), // 
                    new XStreamDumper(new File("./target/dh-report")));
        }
        return context;
    }

}
