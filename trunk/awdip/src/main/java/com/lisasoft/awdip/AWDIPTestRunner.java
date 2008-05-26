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

	    // conformance tests
	    System.out.println("conformance");
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationBoundingBox.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesTestDateAny.class);
	    run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesTestDateBetween.class);

	    // performance tests
            System.out.println("performance");
	    run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationBoundingBox.class);
	    run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesTestDateAny.class);
	    run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesTestDateBetween.class);

	    // load tests
            System.out.println("load");
	    run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationBoundingBox.class);
	    run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationMaximumFeatures.class);
	    run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesTestDateAny.class);
	    run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesTestDateBetween.class);
}
	

}
