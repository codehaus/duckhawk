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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestSuiteListener;

/**
 * A simple listener dumping to the specified print stream (or System.out, if
 * none is provided)
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class PrintStreamListener implements TestSuiteListener {
    protected static final NumberFormat format = new DecimalFormat("0.######");

    protected static final NumberFormat formatTime = new DecimalFormat("0.###");

    protected boolean dumpSingleCalls;

    protected boolean dumpStackTraces;

    protected PrintStream out;

    protected long startTime;

    private long start;

    /**
     * Shortcut for the other constructor, uses {@link System#out} by default
     * 
     * @param dumpSingleCalls
     * @param dumpStackTraces
     */
    public PrintStreamListener(boolean dumpSingleCalls, boolean dumpStackTraces) {
        this(dumpSingleCalls, dumpStackTraces, System.out);
    }

    /**
     * Prints the events occurring during the test on the provided
     * {@link PrintStream}
     * 
     * @param dumpSingleCalls
     *                If true, dumps down even single calls to the
     *                {@link TestExecutor}, otherwise only the start and and
     *                events
     * @param dumpStackTraces
     *                If true, exception stack traces are dumped to output,
     *                otherwise only the error message is. This one depends on
     *                single calls being logged.
     * 
     * @param out
     */
    public PrintStreamListener(boolean dumpSingleCalls,
            boolean dumpStackTraces, PrintStream out) {
        this.dumpSingleCalls = dumpSingleCalls;
        this.dumpStackTraces = dumpStackTraces;
        this.out = out;
    }

    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties properties, double time, Throwable exception) {
        if (!dumpSingleCalls)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(time()).append(") ");
        sb.append(metadata.getProductId()).append(" ").append(
                metadata.getProductVersion());
        sb.append(" - ").append(metadata.getTestId()).append(" ");
        sb.append("(").append(Thread.currentThread().getName()).append(
                "), time ");
        sb.append(format.format(time)).append("s");
        sb.append("\n  Properties: ").append(properties);
        if (exception != null) {
            sb.append("\n  FAILED, ");
            if (dumpStackTraces) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(bos);
                exception.printStackTrace(ps);
                sb.append(new String(bos.toByteArray()));
            } else
                sb.append(exception.getMessage());
        }

        out.println(sb.toString());
    }

    private String time() {
        return formatTime
                .format((System.nanoTime() - startTime) / 1000000000.0);
    }

    public void testRunCompleted(TestMetadata metadata,
            TestProperties properties) {
        String msg = time() + ") " + metadata.getProductId() + " "
                + metadata.getProductVersion() + " - " + metadata.getTestId()
                + ": completed!\n  Properties " + properties;
        out.println(msg);

    }

    public void testRunStarting(TestMetadata metadata,
            TestProperties properties, int callCount) {
        this.startTime = System.nanoTime();
        String msg = metadata.getProductId() + " "
                + metadata.getProductVersion() + " - " + metadata.getTestId()
                + ": started!";
        out.println(msg);
    }

    /**
     * Closes up the listener along with the print stream provided as a
     * parameter (after closing this listener stops working properly)
     */
    public void close() {
        if (out != null) {
            out.flush();
            out.close();
            out = null;
        }
    }

    public void testSuiteStarting(TestContext context) {
        start = System.nanoTime();
        out.println("Test suite for " + context.getProductId() + " version "
                + context.getProductVersion() + " starting");
    }

    public void testSuiteCompleted(TestContext context) {
        long elapsed = System.nanoTime() - start;
        out.println("Test suite for " + context.getProductId() + " version "
                + context.getProductVersion() + " completed, wall time "
                + formatTime.format(elapsed / 1e9) + "s");
        close();
    }

}
