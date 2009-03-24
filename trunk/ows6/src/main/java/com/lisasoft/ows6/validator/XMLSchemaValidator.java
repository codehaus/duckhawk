package com.lisasoft.ows6.validator;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLSchemaValidator implements com.lisasoft.ows6.validator.Validator {
	
	
	private static final Logger log = Logger.getLogger(XMLSchemaValidator.class);
	
	/** 
	 * validates against against the referenced XML schema
	 * 
	 * @param the response that will be validated 
	 */
	public void validate(String response) {

		InputSource is = new InputSource(new StringReader(response));
		Validator v;
		try {
			v = new org.custommonkey.xmlunit.Validator(is);
			v.useXMLSchema(true);
			if (!v.isValid()) {
				throw new ValidationError(v.toString());
			}
		} catch (ConfigurationException e) {
			log.error("ConfigurationException in XMLSchemaValidator", e);
			throw new ValidationError(e);
		} catch (SAXException e) {
			log.error("SAXException in XMLSchemaValidator", e);
			throw new ValidationError(e);
		}
	}
	
	/** 
	 * validates against against the referenced XML schema
	 * 
	 * @param the response that will be validated 
	 */
	public void validate(InputStream response) {

		Validator v;
		try {
			v = new org.custommonkey.xmlunit.Validator(new InputSource(response));
			v.useXMLSchema(true);
			if (!v.isValid()) {
				throw new ValidationError(v.toString());
			}
		} catch (ConfigurationException e) {
			log.error("ConfigurationException in XMLSchemaValidator", e);
			throw new ValidationError(e);
		} catch (SAXException e) {
			log.error("SAXException in XMLSchemaValidator", e);
			throw new ValidationError(e);
		}
	}
	
}
