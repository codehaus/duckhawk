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

import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_SCHEMA_FILE;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_SCHEMA_FOLDER;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.wfsvalidator.WFSValidatorKeys.KEY_TESTS_CONFIG_FILE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import com.lisasoft.wfsvalidator.validator.ValidationError;
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
    
    List<File> schemas;
    File schema;

    /**
     * Initializes the test object
     * 
     * @param context
     *            the context
     * @param typeName
     *            requested type
     * @param maxFeatures
     *            max number of returned features
     * @param testNameSuffix
     */
    public WFSValidatorGenericTest(TestContext context, String request, List<File> schemas, File schema,
            String testNameSuffix) {

        super(context);

        this.setName("testStandardTest");
        this.setTestMethodSuffix(testNameSuffix);
        this.requestBody = request;
        this.schemas = schemas;
        this.schema = schema;
        this.context = context;

    }

    /**
     * Constructor taking the context as parameter
     * 
     * @param context
     */
    public WFSValidatorGenericTest(TestContext context) {
        super(context);
    }
	
    /**
     * Sets up a test suite according to the CSV file.
     * 
     * @param context
     *            Test context
     * @return the test suite
     * @throws IOException
     *             Error reading config file
     * @throws InvalidConfigFileException
     *             Error in config file
     */
    static public Test suite(TestContext context) throws IOException, InvalidConfigFileException {

        String configfile = (String) context.getEnvironment().get(KEY_TESTS_CONFIG_FILE);

        TestSuite suite = null;
        
        List<File> schemas = checkSchemaFolder(context);
        File schema = checkSchemaFile(context);

        if (configfile == null || configfile.equals("")) {
            suite = loadTestsFromFolder(context, schemas, schema);
        } else {
            suite = loadTestsFromCSV(context, schemas, schema);
        }

        return suite;
    }

    private static File checkSchemaFile( TestContext context ) {
        
        File schema = null;
        
        //get a list of all files in the schema folder
        String filename = (String) context.getEnvironment().get(KEY_SCHEMA_FILE);
        
        if(StringUtils.isBlank(filename)) {
            log.info("No local schema configured. Will rely on remote schema.");
        } else {
            log.info("Checking for local schema");
            
            File file = new File(filename);
            log.info("File: " + file.getAbsolutePath());
            
            schema = file;
        }
        return schema;
    }

    private static List<File> checkSchemaFolder( TestContext context ) {
        
        //get a list of all files in the schema folder
        String foldername = (String) context.getEnvironment().get(KEY_SCHEMA_FOLDER);
        List<File> schemas = null;
        
        if(StringUtils.isBlank(foldername)) {
            log.info("No local schemas configured. Will rely on remote schemas.");
        } else {
            log.info("Checking schema folder for local schemas");
            
            File folder = new File(foldername);
            log.info("Folder: "+folder.getAbsolutePath());
            
            if(folder.isDirectory()) {
                schemas = expandFolders(folder);
            } else {
                schemas = new ArrayList<File>();
                schemas.add(folder);
            }
        }
        return schemas;
    }

    private static List<File> expandFolders(File folder) {
        File[] files = folder.listFiles();
        List<File> flist = new ArrayList<File>();
        
        if(files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    // log.info("Folder: "+file.getAbsolutePath());
                    flist.addAll(expandFolders(file));
                } else {
                    // log.info("File: "+file.getAbsolutePath());
                    flist.add(file);
                }
            }
        }
        
        return flist; 
    }

    private static TestSuite loadTestsFromFolder( TestContext context, List<File> schemas, File schema) {
        
        log.info("Loading tests from folder");

        //get a list of all files in the tests folder
        String foldername = (String) context.getEnvironment().get(KEY_TESTS_CONFIG_DIR);
        
        File folder = new File(foldername);
        
        File[] files = folder.listFiles();
        
        //create test suite
        TestSuite suite = new TestSuite();
        int i = 0;
   
        //add tests to suite
        for (File file : files) {
   
            log.info("File: "+file.getAbsolutePath());
   
            WFSValidatorFileTest test = new WFSValidatorFileTest(context, file, schemas, schema, "#"+i);
   
            suite.addTest(test);
            i++;
        }
        
        return suite;
    }

    private static TestSuite loadTestsFromCSV( TestContext context, List<File> schemas, File schema) throws IOException,
            InvalidConfigFileException {
        
        // read CSV file
        String filename = (String) context.getEnvironment()
        .get(KEY_TESTS_CONFIG_DIR) + (String) context.getEnvironment()
        .get(KEY_TESTS_CONFIG_FILE);

        log.info("Loading tests from CSV: " + filename);

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
        		new WFSValidatorGenericTest(context, rq, schemas, schema, "#"+i);
   
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
     * 
     * @throws IOException
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     */
    public void checkStandardTest() throws TransformerFactoryConfigurationError,
            TransformerException, IOException {

        // read received response
        // String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE);
        List<ValidationError> collectedErrors = new ArrayList<ValidationError>();

        try {

            // XML schema validation
            try {
                XMLSchemaValidator xsv = new XMLSchemaValidator();
                xsv.validate(this.response, this.uri, this.schemas, this.schema);
            } catch (ValidationError ve) {
                collectedErrors.add(ve);
            }

            try {
                this.validateSchematron(this.response);
            } catch (ValidationError ve) {
                collectedErrors.add(ve);
            }
            
            if(collectedErrors.size() > 0) {
                String message = "Validation Errors:";
                for(ValidationError ve : collectedErrors) {
                    message += "\n\t- " + ve.getMessage();
                }
                throw new ValidationError(message);
            }

        } catch (ConfigurationException e) {
            log.error("ConfigurationException during XML validation!", e);
        }

        finally {
            this.putCallProperty(TestExecutor.KEY_RESPONSE, formatXML(this.response));
        }
    }

}
