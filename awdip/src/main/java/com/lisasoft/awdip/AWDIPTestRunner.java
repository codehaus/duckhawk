package com.lisasoft.awdip;

import junit.textui.TestRunner;

import com.lisasoft.awdip.tests.conformance.XMLUnitValidationTest;
import com.lisasoft.awdip.tests.conformance.XercesJaxpTest;
import com.lisasoft.awdip.tests.conformance.XercesSaxTest;



public class AWDIPTestRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			    
	
	    //TestRunner.run(WfsPerfTest.class);
	    TestRunner.run(XercesSaxTest.class);
		TestRunner.run(XercesJaxpTest.class);
		TestRunner.run(XMLUnitValidationTest.class);
	           
	}
	

}
