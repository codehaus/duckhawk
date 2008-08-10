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

package com.lisasoft.awdip;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.PerformanceTestRunner;
import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.TestType;
import org.duckhawk.junit3.AbstractDuckHawkTest;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * General tests use this abstract class. The type (performance, conformance,
 * stress) will be set by the corresponding constructor.
 * 
 * @author vmische
 *
 */
public abstract class AbstractAwdipTest extends AbstractDuckHawkTest {
    protected static Communication comm;

    /** data sent to the server (body of the POST message) */
    protected HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    protected Request request;
    
    // for stress tests 
    protected int times;
    private int numThreads;
    private int rampUp;

    // for performance tests
    private double time;
    private Random random;

    private TestType testType;

    /** Date format for XPath expressions */
    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
    /** Date format for filenames (shortened, not special character) */
    public static DateFormat dff = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * Constructor for conformance test
     * @param context
     */
    public AbstractAwdipTest(TestContext context) {
        super(context);
        testType = TestType.conformance;
        setTestClassSuffix("Conformance");        
    }


    /**
     * Constructor for performance test
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times) {
        super(context);
        configureAsPerformanceTest(times);
    }
    
    public void configureAsPerformanceTest(int times) {
        this.times = times;
        testType = TestType.performance;
        setTestClassSuffix("Performance");   
    }


    /**
     * Constructor for performance test (with certain requests within a certain
     * time)
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times, double time,
            Random random) {
        super(context);        
        this.times = times;
        this.time = time;
        this.random = random;
        setTestClassSuffix("Performance");           
    }
    
    /**
     * Constructor for stress test
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times,
            int numThreads, int rampUp) {
        super(context);
        configureAsLoadTest(times, numThreads, rampUp);
    }


    public void configureAsLoadTest(int times, int numThreads, int rampUp) {
        this.times = times;
        this.numThreads = numThreads;
        this.rampUp = rampUp;
        testType = TestType.stress;
        setTestClassSuffix("Load");        
    }
    
   
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH);

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
    }

    
    protected String sanitizeForFilename(String input) {
        return input.replace(':', '_');
    }


    protected TestRunner getTestRunner(TestContext context) {
        TestRunner runner;
        
        switch (testType) {
        case conformance:
            runner = new ConformanceTestRunner(context, buildTestExecutor());            
            break;
        case performance:
            if (random != null)
                runner = new PerformanceTestRunner(context, buildTestExecutor(),
                        times, time, random);
            else
                runner = new PerformanceTestRunner(context, buildTestExecutor(),
                        times);
            break;
        case stress:
        default:
            runner = new StressTestRunner(context, buildTestExecutor(), times,
                    numThreads, rampUp);
            break;
        }
        return runner;
    }


    public TestType getTestType() {
        return testType;
    }
}
