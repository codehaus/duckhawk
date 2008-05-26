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
	           
            TestRunner.run(com.lisasoft.awdip.tests.performance.SiteSinglePhenomTimeSeriesMaximumFeatures.class);
	}
	

}
