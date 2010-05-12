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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


public class XMLSchemaValidator  {
	
	
	private static final Logger log = Logger.getLogger(XMLSchemaValidator.class);
	
	/** 
	 * validates against against the referenced XML schema
	 * 
	 * @param the request which will be validated and the uri it came from. 
	 */
	public void validate(String input, String uri) throws IOException{
		try {
			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = factory.newSchema();
			StringReader reader = new StringReader(input);
			Source ss = new StreamSource(reader, uri);
			Validator v = schema.newValidator();
			v.validate(ss);
        }
        catch (SAXException ex) {
            log.error("XML does not validate: ");
            log.error(ex.getMessage());
        }  
	}
	
	
}
