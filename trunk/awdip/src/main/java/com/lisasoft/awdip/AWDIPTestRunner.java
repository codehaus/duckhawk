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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.lisasoft.awdip.tests.reliability.ReliabilityAggregator;

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

		TrustManager[] trustAllCerts = new TrustManager[]{
		        new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		        }
		    };
		
		try {
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch (Exception e) {
	        
	    }

		if (args.length > 0) {

			if ("test".equals(args[0])) {
				
				


				URL url = new URL("https://www.seegrid.csiro.au/subversion/xmml/AWDIP/trunk/geoserver_conf/commonSchemas/awdip.xsd");
				InputStream in = url.openStream();

				// Create a buffered input stream for efficency
				BufferedInputStream bufIn = new BufferedInputStream(in);

				// Repeat until end of file
				for (;;)
				{
					int data = bufIn.read();

					// Check for EOF
					if (data == -1)
						break;
					else
						System.out.print ( (char) data);
				}
			}




			if ("reliability".equals(args[0])) {
				runReliability(args);
			}


		} else {

			System.out.println("Starting test suite!");

			//TestRunner.run(WfsPerfTest.class);
			//TestRunner.run(XercesSaxTest.class);
			//TestRunner.run(XercesJaxpTest.class);
			//TestRunner.run(XMLUnitValidationTest.class);
			/*
            run(com.lisasoft.awdip.tests.general.SiteSinglePhenomDateAnyTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteSinglePhenomDateBetweenTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxAndMaximumFeaturesTest.suite());
            run(com.lisasoft.awdip.tests.general.SiteLocationMaximumFeaturesTest.suite());
			 */

			try {
				run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxAndMaximumFeaturesTest.suite());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxTest.suite());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			try {
				run(com.lisasoft.awdip.tests.general.SiteLocationMaximumFeaturesTest.suite());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			/*            
            try {
                run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxAndMaximumFeaturesTest.suite());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                run(com.lisasoft.awdip.tests.general.SiteLocationBoundingBoxTest.suite());
            } catch (Exception e) {
                    System.out.println(e.getMessage());
            }
			 */           
		}
	}

	private static void runReliability(String args[]) throws Exception {
		int testCount = 20;

		if (args.length > 1) {
			try {
				testCount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("Couldn't parse 2nd argument. Starting reliability tets with "+testCount+".");
			}
		}

		System.out.println("Starting reliability tests!");
		ReliabilityAggregator ra = new ReliabilityAggregator();
		ra.start(testCount);
	}

	private static void runError(String args[]) {

		System.out.println("Starting error tests!");

		try {
			run(new com.lisasoft.awdip.tests.error.SiteLocationErrorTest());
			run(new com.lisasoft.awdip.tests.error.SiteSinglePhenomTimeErrorTest());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
