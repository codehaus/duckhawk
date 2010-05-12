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


import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_GS_PATH;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_HOST;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_PORT;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.TestType;
import org.duckhawk.junit3.AbstractDuckHawkTest;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;
import com.lisasoft.wfsvalidator.WFSValidatorKeys;
import com.lisasoft.wfsvalidator.validator.SchematronValidator;
import com.lisasoft.wfsvalidator.validator.ValidationError;

/**
 * An abstract class providing the generic functionality of
 * a test used for OWS6.
 * 
 * This includes mainly the XML schema and Schematron validation
 * 
 * 
 * @author shansen - www.lisasoft.com - Oct 10, 2008
 *
 */
public class WFSValidatorAbstractTest extends AbstractDuckHawkTest {
	
	private static final Logger log = Logger.getLogger(WFSValidatorAbstractTest.class);
	
	//Schematron response without any errors
	public static final String EMPTY_SCHEMATRON_RESULT = "";

	//data sent to the server (body of the POST message) 
	protected HashMap<String, String> data = new HashMap<String, String>();
	//Request sent to the server
	protected Request request;
	protected TestContext context;
	protected Communication comm;

	
	/**
	 * Constructor taking the context as parameter
	 * 
	 * @param context
	 */
	public WFSValidatorAbstractTest(TestContext context) {
		super(context);
		this.context = context;
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String host = (String) getEnvironment(KEY_HOST);
		int port = (Integer) getEnvironment(KEY_PORT);
		String path = (String) getEnvironment(KEY_GS_PATH);

		this.comm = new Communication(host, port);
		this.request = new Request(RequestMethod.POST, "/" + path);
	}

	/**
	 * Returns a test runner
	 */
	protected TestRunner getTestRunner(TestContext context) {
		TestRunner runner;
		runner = new ConformanceTestRunner(context, buildTestExecutor());            
		return runner;
	}

	
	/**
	 * Returns the type of test
	 * 
	 * @return TestType.conformance
	 */
	public TestType getTestType() {
		return TestType.conformance;
	}


	/** 
	 * validates against a set of schematron rules
	 * 
	 * @param the response that will be validated 
	 * @throws ValidationError 
	 */
	public void validateSchematron(String response) throws ValidationError {
		
		//load schematron files
		String folder =   (String)this.context.getEnvironment().get(WFSValidatorKeys.KEY_SCHEMATRON_FOLDER); 
		String extension = (String)this.context.getEnvironment().get(WFSValidatorKeys.KEY_SCHEMATRON_FILE_EXTENSION);
		String transformerFile =   (String)this.context.getEnvironment().get(WFSValidatorKeys.KEY_SCHEMATRON_FOLDER) 
				 + (String)this.context.getEnvironment().get(WFSValidatorKeys.KEY_SCHEMATRON_TRANSFORMER);
		
		SchematronValidator sv = new SchematronValidator(folder, extension, transformerFile);
		sv.validate(response);
		
	}
	
	
	/**
	 * Formats the XML document in the given String
	 * 
	 * @param xml unformatted XML document
	 * @return formatted XML document
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String formatXML (String xml) {
		
		String prettyResponse = xml;
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			Source source = new StreamSource(new StringReader(xml));
		
			transformer.transform(source, result);
			prettyResponse = result.getWriter().toString();
		} catch (TransformerFactoryConfigurationError e) {
			log.debug("TransformerFactoryConfigurationError:\n"+xml);
		} catch (TransformerException e) {
			log.debug("TransformerException:\n"+xml);
		}
		
		log.debug(prettyResponse);
		
		
		return prettyResponse;
	}
	
	

}
