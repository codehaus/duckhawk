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

import java.util.Set;

import junit.framework.Assert;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.easymock.EasyMock;

/**
 * Helper class to do the same kind of testing over and over against different
 * exception sequences
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class ConformanceSummarizerTestScaffolding {

    Throwable[] exceptions;

    ConformanceSummarizer summarizer;

    TestMetadata metadata;

    TestExecutor executor;

    public ConformanceSummarizerTestScaffolding(Throwable... exceptions) {
        this.summarizer = new ConformanceSummarizer();
        this.exceptions = exceptions;
        this.metadata = new TestMetadata("whosGonnaTestTheTests", "0.1", "test", TestType.conformance);
        this.executor = EasyMock.createNiceMock(TestExecutor.class);
    }

    public Set<String> performTests() throws AssertionError {
        Set<String> messagesApi = performTestDirectApi();
        Set<String> messagesListener = performTestListener();
        Assert.assertEquals(messagesApi, messagesListener);
        return messagesApi;
    }

    private Set<String> performTestDirectApi() {
        summarizer.start();
        int count = 0;
        for (Throwable exception : exceptions) {
            if (exception != null)
                count++;
            summarizer.accumulate(exception);
        }

        Assert.assertEquals(count, summarizer.getErrorCount());
        Assert.assertEquals(exceptions.length, summarizer.getCallCount());
        if (exceptions.length > 0)
            Assert.assertEquals(new Double(count * 1.0 / exceptions.length), summarizer
                    .getErrorPercentage());
        else
            Assert.assertEquals(0.0, summarizer.getErrorPercentage());

        return summarizer.getUniqueExceptionMessages();
    }

    private Set<String> performTestListener() {
        summarizer.testRunStarting(metadata, null, exceptions.length);
        int count = 0;
        for (Throwable exception : exceptions) {
            if (exception != null)
                count++;
            summarizer.testCallExecuted(executor, metadata, null, 0.0,
                    exception);
        }
        TestProperties props = new TestPropertiesImpl();
        summarizer.testRunCompleted(metadata, props);

        Assert.assertEquals(new Integer(count), props
                .get(TestExecutor.KEY_ERROR_COUNT));
        Assert.assertEquals(new Integer(exceptions.length), props
                .get(TestExecutor.KEY_CALL_COUNT));
        if (exceptions.length > 0)
            Assert.assertEquals(new Double(count * 1.0 / exceptions.length), props
                    .get(TestExecutor.KEY_ERROR_PERCENTAGE));
        else
            Assert.assertEquals(new Double(0.0), props
                    .get(TestExecutor.KEY_ERROR_PERCENTAGE));

        return (Set<String>) props.get(TestExecutor.KEY_ERROR_SUMMARY);
    }
}
