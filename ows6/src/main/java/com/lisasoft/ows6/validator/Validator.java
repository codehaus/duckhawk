package com.lisasoft.ows6.validator;

/**
 * Interface for validator that validate
 * a server's response.
 * 
 * @author shansen
 *
 */
public interface Validator {

	/**
	 * Validating a server's response.
	 * 
	 * @param input response document
	 */
	public void validate(String input);
	
}
