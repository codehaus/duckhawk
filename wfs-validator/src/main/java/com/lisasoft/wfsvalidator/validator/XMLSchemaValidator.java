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

package com.lisasoft.wfsvalidator.validator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.lisasoft.wfsvalidator.tests.WFSValidatorAbstractTest;

public class XMLSchemaValidator {

    private static final Logger log = Logger.getLogger(XMLSchemaValidator.class);

    /**
     * validates against against the referenced XML schema
     * 
     * @param the
     *            request which will be validated and the uri it came from.
     */
    public void validate(String input, List<File> schemas, File schemaFile) throws IOException{
        validate(input, null, schemas, schemaFile);
    }

    /**
     * validates against against the referenced XML schema
     * 
     * @param the
     *            XML which will be validated.
     */
    public void validate(String input, String uri, List<File> schemas, File schemaFile) throws IOException {
        try {
            if(uri == null) {
                log.info("Starting XML Schema Validation");
            } else {
                log.info("Starting XML Schema Validation for '" + uri + "'");
            }

            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            ResourceResolver rr = new ResourceResolver();
            rr.setSchemas(schemas);
            factory.setResourceResolver(rr);
            Schema schema;
            String result = null;
            
            log.info("Validating against local schemas");
            try {
                if(schemaFile == null) {
                    log.info("Using remote schemas");
                    schema = factory.newSchema();
                } else {
                    log.info("Schema: " + schemaFile.getName());
                    schema = factory.newSchema(schemaFile);
                }
                StringReader reader = new StringReader(input);
                Source ss;
                if(uri == null) {
                    ss = new StreamSource(reader);
                } else {
                    ss = new StreamSource(reader, uri);
                }
                Validator v = schema.newValidator();

                v.validate(ss);
            } catch (SAXException ex) {
                try {
                    result = "XML does not validate: " + ex.getMessage();
                    log.error(result);

                    throw new ValidationError(WFSValidatorAbstractTest.formatXML(result));
                } catch (TransformerFactoryConfigurationError e) {
                    log.error("TransformerFactoryConfigurationError during SchemaValidation", e);
                    if(result != null) {
                        throw new ValidationError(result);
                    }
                }                            
            }
        } catch (ValidationError ve) {
            throw ve;
        } catch (Throwable t) {
            log.error("XML does not validate: ");
            log.error(t.getMessage());
        } finally {
            log.info("Finished XML Schema Validation");
        }
    }

}
