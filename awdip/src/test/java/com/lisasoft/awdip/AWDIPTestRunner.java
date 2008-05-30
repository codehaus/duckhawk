package com.lisasoft.awdip;

import junit.textui.TestRunner;


public class AWDIPTestRunner {
    
        private static void run(Class testClass) {
            System.out.println(testClass.getSimpleName());
            TestRunner.run(testClass);
        }
    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			    
	
	    //TestRunner.run(WfsPerfTest.class);
	    //TestRunner.run(XercesSaxTest.class);
		//TestRunner.run(XercesJaxpTest.class);
		//TestRunner.run(XMLUnitValidationTest.class);
	    
	    
	    run(com.lisasoft.awdip.tests.general.DateBetweenParametrizedTest.class);
/*
	    // conformance tests
	    System.out.println("conformance");
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationLocationBoundingBoxTest.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationLocationBoundingBoxAndMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationLocationMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesDateAnyTest.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesDateBetweenTest.class);

	    // performance tests
            System.out.println("performance");
	    run(com.lisasoft.awdip.tests.performance.SiteLocationLocationBoundingBoxTest.class);
	    run(com.lisasoft.awdip.tests.performance.SiteLocationLocationBoundingBoxAndMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.performance.SiteLocationLocationMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesDateAnyTest.class);
	    run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesDateBetweenTest.class);

	    // load tests
            System.out.println("load");
	    run(com.lisasoft.awdip.tests.load.SiteLocationLocationBoundingBoxTest.class);
	    run(com.lisasoft.awdip.tests.load.SiteLocationLocationBoundingBoxAndMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.load.SiteLocationLocationMaximumFeaturesTest.class);
	    run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesDateAnyTest.class);
	    run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesDateBetweenTest.class);
*/	    
}
	

}
