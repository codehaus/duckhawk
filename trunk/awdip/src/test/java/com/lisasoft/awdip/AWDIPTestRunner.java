package com.lisasoft.awdip;

import junit.framework.Test;
import junit.textui.TestRunner;


public class AWDIPTestRunner {
    
        private static void run(Test testSuite) {
            System.out.println(testSuite.getClass().getSimpleName());
            TestRunner.run(testSuite);
        }

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
			    
	
	    //TestRunner.run(WfsPerfTest.class);
	    //TestRunner.run(XercesSaxTest.class);
		//TestRunner.run(XercesJaxpTest.class);
		//TestRunner.run(XMLUnitValidationTest.class);
	    
            run(com.lisasoft.awdip.tests.general.SiteSinglePhenomDateAnyParametrizedTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteSinglePhenomDateBetweenParametrizedTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxAndMaximumFeaturesTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationMaximumFeaturesTest.suite());
	}
}
