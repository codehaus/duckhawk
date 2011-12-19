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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

import com.lisasoft.wfsvalidator.validator.ValidationError;
import com.lisasoft.wfsvalidator.validator.XMLSchemaValidator;

/**
 * Performs a WFS validation test from a file on disk instead of from a WFS service. 
 * 
 * @author jgroffen
 *
 */
public class WFSValidatorFileTest extends WFSValidatorGenericTest {

    private static final Logger log = Logger.getLogger(WFSValidatorFileTest.class);

    File file;

    /**
     * Initializes the test object
     * 
     * @param context
     *            the context
     * @param file
     * @param testNameSuffix
     */
    public WFSValidatorFileTest(TestContext context, File file, List<File> schemas, File schema,
            String testNameSuffix) {
        super(context);

        this.setName("testStandardTest");
        this.setTestMethodSuffix(testNameSuffix);
        this.requestBody = null;
        this.file = file;
        this.schemas = schemas;
        this.schema = schema;
        this.context = context;
    }

    /**
     * Initializes a test:
     * 
     * Creates the request to be sent and stores information in the context
     * 
     * @param properties
     *            Test properties
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     */
    public void initStandardTest(TestProperties properties)
            throws TransformerFactoryConfigurationError, TransformerException {

        // create simple request
        this.data.put("file", this.file.getAbsolutePath());

        // properties.put(TestExecutor.KEY_REQUEST, formatXML(this.requestBody));
        properties.put(TestExecutor.KEY_DESCRIPTION,
                "Part of the standard test class. This is a test" + "using the file "
                        + this.file.getAbsolutePath() + ".");
    }

    /**
     * Runs the actual test:
     * 
     * Loads the file to test into the context.
     * 
     * @throws IOException
     *             Problems sending the request
     * @throws TransformerException
     * @throws TransformerFactoryConfigurationError
     */
    public void testStandardTest() throws HttpException, IOException,
            TransformerFactoryConfigurationError, TransformerException {

        log.debug("file: " + this.data);

        // this.streamsource = comm.sendStreamSourceResponse(this.request, this.data);
        // this.response = inputStreamAsString(this.streamsource.getInputStream());
        // this.response = inputStreamAsString(comm.sendRequestStreamResponse(this.request,
        // this.data));
        this.response = inputStreamAsString(new FileInputStream(this.file));
        this.putCallProperty(TestExecutor.KEY_RESPONSE, formatXML(response));
    }

    /**
     * Returns the content of the given InputStream as String.
     * 
     * @param stream
     *            the InputStream
     * @return the content of the InputStream as String
     * @throws IOException
     */
    public static String inputStreamAsString(InputStream stream) throws IOException {
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
                xsv.validate(this.response, schemas, schema);
            } catch (ValidationError ve) {
                collectedErrors.add(ve);
            }

            // schematron validation
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
