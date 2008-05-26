package com.lisasoft.awdip;

import junit.textui.TestRunner;


public class AWDIPTestRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			    
	
	    //TestRunner.run(WfsPerfTest.class);
	    //TestRunner.run(XercesSaxTest.class);
		//TestRunner.run(XercesJaxpTest.class);
		//TestRunner.run(XMLUnitValidationTest.class);

	    // conformance tests
            TestRunner.run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationBoundingBox.class);
            TestRunner.run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.conformance.SiteLocationTestLocationMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesTestDateAny.class);
            TestRunner.run(com.lisasoft.awdip.tests.conformance.SiteSinglePhenomTimeSeriesTestDateBetween.class);

            // performance tests
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationBoundingBox.class);
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteLocationTestLocationMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesTestDateAny.class);
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesTestDateBetween.class);

            // load tests
            TestRunner.run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationBoundingBox.class);
            TestRunner.run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationBoundingBoxAndMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.load.SiteLocationTestLocationMaximumFeatures.class);
            TestRunner.run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesTestDateAny.class);
            TestRunner.run(com.lisasoft.awdip.tests.load.SiteSinglePhenomTimeSeriesTestDateBetween.class);
}
	

}
