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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.easymock.EasyMock;

/**
 * @author   Andrea Aime (TOPP)
 */
public class PrintStreamListenerTest extends TestCase {

    private ByteArrayOutputStream bos;

    private PrintStream ps;

    private TestMetadata metadata;

    public void setUp() {
        bos = new ByteArrayOutputStream();
        ps = new PrintStream(bos);
        metadata = new TestMetadata("product", "0.1", "test", TestType.undetermined);
    }

    public void testStartup() {
        PrintStreamListener listener = new PrintStreamListener(true, true, ps);
        listener.testRunStarting(metadata, new TestPropertiesImpl(), 20);
        String result = bos.toString();
        assertTrue(result.contains(metadata.getTestId()));
        assertTrue(result.contains(metadata.getProductId()));
        assertTrue(result.contains(metadata.getProductVersion()));
    }

    public void testEnd() {
        PrintStreamListener listener = new PrintStreamListener(true, true, ps);
        listener.testRunCompleted(metadata, new TestPropertiesImpl());
        String result = bos.toString();
        assertTrue(result.contains(metadata.getTestId()));
        assertTrue(result.contains(metadata.getProductId()));
        assertTrue(result.contains(metadata.getProductVersion()));
    }
    
    public void testCall() {
        PrintStreamListener listener = new PrintStreamListener(true, false, ps);
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        listener.testCallExecuted(executor, metadata, new TestPropertiesImpl(),
                12.5, null);
        String result = bos.toString();
        // System.out.println(result);
        assertTrue(result.contains(metadata.getTestId()));
        assertTrue(result.contains(metadata.getProductId()));
        assertTrue(result.contains(metadata.getProductVersion()));
        assertTrue(result.contains("12.5") || result.contains("12,5"));
    }

    public void testCallExceptionNoStackTrace() {
        PrintStreamListener listener = new PrintStreamListener(true, false, ps);
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        Exception e = new Exception("This is the exception message");
        e.fillInStackTrace();
        listener.testCallExecuted(executor, metadata, new TestPropertiesImpl(),
                12.5, e);
        String result = bos.toString();
        // System.out.println(result);
        assertTrue(result.contains(metadata.getTestId()));
        assertTrue(result.contains(metadata.getProductId()));
        assertTrue(result.contains(metadata.getProductVersion()));
        assertTrue(result.contains("12.5") || result.contains("12,5"));
        assertTrue(result.contains("This is the exception message"));
        assertFalse(result.contains("at line"));
    }

    public void testCallExceptionWithStackTrace() {
        PrintStreamListener listener = new PrintStreamListener(true, true, ps);
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        Exception e = new Exception("This is the exception message");
        e.fillInStackTrace();
        listener.testCallExecuted(executor, metadata, new TestPropertiesImpl(),
                12.5, e);
        String result = bos.toString();
        // System.out.println(result);
        assertTrue(result.contains(metadata.getTestId()));
        assertTrue(result.contains(metadata.getProductId()));
        assertTrue(result.contains(metadata.getProductVersion()));
        assertTrue(result.contains("12.5") || result.contains("12,5"));
        assertTrue(result.contains("This is the exception message"));
        assertTrue(result
                .contains("at org.duckhawk.util.PrintStreamListenerTest.testCallExceptionWithStackTrace"));
    }
    
    public void testDisableCallLogs() {
        PrintStreamListener listener = new PrintStreamListener(false, true, ps);
        TestExecutor executor = EasyMock.createNiceMock(TestExecutor.class);
        Exception e = new Exception("This is the exception message");
        e.fillInStackTrace();
        listener.testCallExecuted(executor, metadata, new TestPropertiesImpl(),
                12.5, e);
        String result = bos.toString();
        // System.out.println(result);
        assertEquals("", result);
    }

}
