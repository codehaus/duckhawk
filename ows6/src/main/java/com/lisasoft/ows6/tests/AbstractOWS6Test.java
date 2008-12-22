package com.lisasoft.ows6.tests;


import static com.lisasoft.ows6.OWS6Keys.KEY_GS_PATH;
import static com.lisasoft.ows6.OWS6Keys.KEY_HOST;
import static com.lisasoft.ows6.OWS6Keys.KEY_PORT;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.TestType;
import org.duckhawk.junit3.AbstractDuckHawkTest;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;
import com.lisasoft.ows6.OWS6Keys;
import com.lisasoft.ows6.validator.SchematronValidator;
import com.lisasoft.ows6.validator.ValidationError;

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
public class AbstractOWS6Test extends AbstractDuckHawkTest {
	
	private static final Logger log = Logger.getLogger(AbstractOWS6Test.class);
	
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
	public AbstractOWS6Test(TestContext context) {
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
		String folder =   (String)this.context.getEnvironment().get(OWS6Keys.KEY_SCHEMATRON_FOLDER); 
		String extension = (String)this.context.getEnvironment().get(OWS6Keys.KEY_SCHEMATRON_FILE_EXTENSION);
		String transformerFile =   (String)this.context.getEnvironment().get(OWS6Keys.KEY_SCHEMATRON_FOLDER) 
				 + (String)this.context.getEnvironment().get(OWS6Keys.KEY_SCHEMATRON_TRANSFORMER);
		
		SchematronValidator sv = new SchematronValidator(folder, extension, transformerFile);
		sv.validate(response);
		
	}

}
