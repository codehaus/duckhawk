package com.lisasoft.ows6.validator;

import java.io.StringReader;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLSchemaValidator implements com.lisasoft.ows6.validator.Validator {

	
	
	
	/** 
	 * validates against against a community schema
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
			throw new ValidationError(e);
		} catch (SAXException e) {
			throw new ValidationError(e);
		}
		
		
		

	}
	
}
