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

package com.lisasoft.wfsvalidator;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.duckhawk.core.DefaultTestContext;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.InvalidConfigFileException;
import com.lisasoft.awdip.util.SetPropertyListener;
import com.lisasoft.awdip.util.Util;
import com.lisasoft.wfsvalidator.listener.WFSValidatorTransformHtmlListener;

/**
 * Test runner for the OWS6 GML profile validation test suite.
 * 
 * Executable class that runs all tests.
 * 
 * 
 * @author shansen - www.lisasoft.com - Oct 6, 2008
 *
 */
public class WFSValidatorTestRunner {

	private static final Logger log = Logger.getLogger(WFSValidatorTestRunner.class);

	//name of the properties file
	public static final String OWS6_PROPERTIES_FILE = "WFSValidator.properties";
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";



	/**
	 * Main method of the runner.
	 * 
	 * - Loads the context
	 * - Starts tests suites
	 * 
	 * @param args
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	public static void main(String[] args)  {

		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		TestContext context;

		try {

			//Load context
			context = getContext(OWS6_PROPERTIES_FILE, args);
			//Start tests suites
			//run(com.lisasoft.wfsvalidator.tests.StandardOWS6Test.suite(context));
			run(com.lisasoft.wfsvalidator.tests.WFSValidatorGenericTest.suite(context));

		} catch (ConfigurationException e) {
			log.error("ConfigurationExciption in main()", e);
		} catch (IOException e) {
			log.error("IOException in main()", e);
		} catch (InvalidConfigFileException e) {
			log.error("InvalidConfigFileException in main()", e);
		}

	}


	/**
	 * Loads the property file and creates the context for the tests
	 * from it.
	 * 
	 * @param filename : name of the property file
	 * @param forcePropertyOutputLocal : Properties to be put into the report
	 * @return : the test context
	 * @throws ConfigurationException : thrown if the file couldn't be read
	 */
	static TestContext getContext(String filename, String[] forcePropertyOutputLocal) 
	throws ConfigurationException {

		//reading the property file
		Configuration config = new PropertiesConfiguration(filename);

		//setting the test environment
		TestProperties environment = new TestPropertiesImpl();

		environment.put(WFSValidatorKeys.KEY_HOST, config.getString("host"));
		environment.put(WFSValidatorKeys.KEY_PORT, config.getInt("port"));
		environment.put(WFSValidatorKeys.KEY_GS_PATH, config.getString("serverPath"));

		environment.put(WFSValidatorKeys.KEY_TESTS_CONFIG_DIR,
				config.getString("testsConfigDir"));
		environment.put(WFSValidatorKeys.KEY_TESTS_CONFIG_FILE,
				config.getString("testsConfigFile"));
		environment.put(WFSValidatorKeys.KEY_SCHEMA_RPATH, "http://schemas.opengis.net/wfs/1.1.0/wfs.xsd");

		environment.put(WFSValidatorKeys.KEY_SCHEMATRON_FOLDER, config.getString("schematronFolder"));
		environment.put(WFSValidatorKeys.KEY_SCHEMATRON_FILE_EXTENSION, config.getString("schematronFilesExtension"));
		environment.put(WFSValidatorKeys.KEY_SCHEMATRON_TRANSFORMER, config.getString("schematronTransformer"));

		//setting which properties are put into the report
		String[] forcePropertyOutputGlobal = new String[] {
				TestExecutor.KEY_REQUEST, TestExecutor.KEY_RESPONSE};
		String[] forcePropertyOutput;

		forcePropertyOutput = Util.concatStringArrays(
				forcePropertyOutputGlobal, forcePropertyOutputLocal);

		//creating the context
		TestContext context = new DefaultTestContext("WFSValidator", "1.0", environment,
				new PerformanceSummarizer(),
				new ConformanceSummarizer(true),
				new SetPropertyListener(forcePropertyOutput),
				//new PrintStreamListener(true, true),
				new PrintStreamListener(false, true),
				//new XStreamDumper(new File(config.getString("reportXmlDir"))),
				//new TransformHtmlListener(new File(config.getString("reportHtmlDir")))
				new WFSValidatorTransformHtmlListener(new File(config.getString("reportXmlDir")), new File(config.getString("reportHtmlDir")))
				);

		return context;

	}


	/**
	 * Runs a test suite.
	 * 
	 * @param testSuite the suite to be run
	 */
	private static void run(Test testSuite) {
		log.info(testSuite.getClass().getSimpleName());
		TestRunner.run(testSuite);
	}
}
