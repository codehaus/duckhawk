package com.lisasoft.awdip.tests.reliability;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxAndMaximumFeaturesTest;
import com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxTest;

public class ReliabilityAggregator {
    Random random = new Random();
    TestSuite suite = new TestSuite();
    int numberOfTests;
    int rand;
    
    /** Date format for prepending to methods with milli seconds */
    public static DateFormat dfMilli = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public ReliabilityAggregator() throws Exception {
        // get all tests, run them randomly with defined seed
        Enumeration<Test> tests = ((TestSuite)SiteLocationBoundingBoxTest.suite()).tests();
        while(tests.hasMoreElements()) {suite.addTest(tests.nextElement());}
        
        tests = ((TestSuite)SiteLocationBoundingBoxAndMaximumFeaturesTest.suite()).tests();
        while(tests.hasMoreElements()) {suite.addTest(tests.nextElement());}

        numberOfTests = suite.testCount();
        random.setSeed(100);
    }
    
    
    private void run(Test test) {
        System.out.println(test.getClass().getSimpleName());
        TestRunner.run(test);
    }
    
    /**
     * Start the reliability test
     * 
     * @param numberOfRandomTests Number of tests that should be performed
     */
    public void start(int numberOfRandomTests) {
        for (int i=0; i<numberOfRandomTests; i++) {
            rand = random.nextInt(numberOfTests);
            AbstractAwdipTest test = (AbstractAwdipTest)suite.testAt(rand);
            
            // prepend time to ensure test run twice doesn't overwrite the
            // results
            String testMethodSuffix = test.getTestMethodSuffix();
            test.setTestMethodSuffix(testMethodSuffix + "_"
                    + dfMilli.format(new Date(System.currentTimeMillis())));
            run(test);
            test.setTestMethodSuffix(testMethodSuffix);
        }
    }
}
