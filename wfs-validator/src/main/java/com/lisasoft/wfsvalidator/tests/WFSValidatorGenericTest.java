/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 LISAsoft- http://www.lisasoft.com.
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

package com.lisasoft.wfsvalidator.tests;

import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_TESTS_CONFIG_FILE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import com.lisasoft.wfsvalidator.validator.XMLSchemaValidator;

/**
 * Alternative test class to StandardOWS6Test. Reads the complete
 * request from CSV file instead of building a WFS request from
 * parameters. 
 * 
 * @author shansen
 *
 */
public class WFSValidatorGenericTest extends WFSValidatorAbstractTest {

	private static final Logger log = Logger.getLogger(WFSValidatorGenericTest.class);

	String requestBody;
	String response;
	String uri;


	/**
	 * Initializes the test object
	 * 
	 * @param context the context
	 * @param typeName requested type
	 * @param maxFeatures max number of returned features
	 * @param testNameSuffix
	 */
	public WFSValidatorGenericTest(TestContext context, String request, String testNameSuffix) {

		super(context);

		this.setName("testStandardTest");
		this.setTestMethodSuffix(testNameSuffix);

		this.requestBody = request;
		this.context = context;

	}

	/**
	 * Sets up a test suite according to the CSV file.
	 * 
	 * @param context Test context
	 * @return the test suite
	 * @throws IOException Error reading config file
	 * @throws InvalidConfigFileException Error in config file
	 */
	static public Test suite(TestContext context) throws IOException, InvalidConfigFileException {

		// read CSV file
		String filename = (String) context.getEnvironment()
		.get(KEY_TESTS_CONFIG_DIR) + (String) context.getEnvironment()
		.get(KEY_TESTS_CONFIG_FILE);
		CSVReader csv = new CSVReader(new File(filename));      

		List<String[]> lines = csv.getLines();

		//if CSV file is empty
		if (lines==null) {
			log.error("WFSValidatorGenericTest: File doesn't contain any data!");
			throw new InvalidConfigFileException("File doesn't contain any data!");
		}

		// remove header
		lines.remove(0);

		//to store test parameters
		String[] CSVrequests = new String[lines.size()];

		//read test parameters
		for (int i=0; i<lines.size(); i++) {

			String[] line = lines.get(i);

			CSVrequests[i] = line[0];

		}

		//create test suite
		TestSuite suite = new TestSuite();
		int i = 0;

		//add tests to suite
		for (String rq : CSVrequests) {

			log.debug("Request: "+rq);

			WFSValidatorGenericTest test = 
				new WFSValidatorGenericTest(context, rq, "#"+i);

			suite.addTest(test);
			i++;
		}

		return suite;
	}


	/**
	 * Initializes a test:
	 * 
	 * Creates the request to be sent and 
	 * stores information in the context 
	 * 
	 * @param properties Test properties
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void initStandardTest(TestProperties properties) throws TransformerFactoryConfigurationError, TransformerException {

		//create simple request
		this.data.put("body", this.requestBody);

		properties.put(TestExecutor.KEY_REQUEST, formatXML(this.requestBody));
		properties.put(TestExecutor.KEY_DESCRIPTION,
				"Part of the standard test class. This is a test" +
				"with the request "+this.requestBody + ".");
	}

	/**
	 * Runs the actual test:
	 * 
	 * Sends request to server.
	 * The response is stored in the context
	 * 
	 * @throws HttpException Problems with Http connection
	 * @throws IOException Problems sending the request
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void testStandardTest() throws HttpException, IOException, TransformerFactoryConfigurationError, TransformerException {

		log.debug("request: "+this.request.getPath());
		log.debug("data: "+this.data);
		
		//this.streamsource = comm.sendStreamSourceResponse(this.request, this.data);
		//this.response = inputStreamAsString(this.streamsource.getInputStream());
		this.response = inputStreamAsString(comm.sendRequestStreamResponse(this.request, this.data));
		this.uri = comm.sendUriAsString(this.request);
		this.putCallProperty(TestExecutor.KEY_RESPONSE, formatXML(response));
	}

	
	/**
	 * Returns the content of the given InputStream as String.
	 * 
	 * @param stream the InputStream
	 * @return the content of the InputStream as String
	 * @throws IOException
	 */
	public static String inputStreamAsString(InputStream stream)
	throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}

		br.close();
		return sb.toString();
	}
	
	
	/**
	 * Checks and validates the response
	 * @throws IOException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	public void checkStandardTest() throws TransformerFactoryConfigurationError, TransformerException, IOException {

		//read received response
		//String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE);

		try {

			//XML schema validation
			XMLSchemaValidator xsv = new XMLSchemaValidator();
			xsv.validate(this.response, this.uri);
		
			
			//schematron validation
			this.validateSchematron(this.response);

		} catch (ConfigurationException e) {
			log.error("ConfigurationException during XML validation!", e);
		}
		
		finally{
			this.putCallProperty(TestExecutor.KEY_RESPONSE, formatXML(this.response));
		}
	}

}
