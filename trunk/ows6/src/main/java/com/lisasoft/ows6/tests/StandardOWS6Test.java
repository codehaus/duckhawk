package com.lisasoft.ows6.tests;

import static com.lisasoft.ows6.OWS6Keys.KEY_TESTS_CONFIG_DIR;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import com.lisasoft.ows6.OWS6Keys;
import com.lisasoft.ows6.validator.XMLSchemaValidator;


/**
 * An example test for OWS6
 * 
 * It sends a very simple WFS getFeatures request to a server
 * (specified in the context)
 * 
 * @author shansen - www.lisasoft.com - Oct 10, 2008
 *
 */
public class StandardOWS6Test extends AbstractOWS6Test {

	private static final Logger log = Logger.getLogger(StandardOWS6Test.class);
	
	/** CSV File containing test configuration
	  * Expected content: type_name,max_features */
	final static String CONFIG_FILE = "/StandardOWS6Test.csv";

	String typeName;
	int maxFeatures;


	/**
	 * Initializes the test object
	 * 
	 * @param context the context
	 * @param typeName requested type
	 * @param maxFeatures max number of returned features
	 * @param testNameSuffix
	 */
	public StandardOWS6Test(TestContext context, String typeName, int maxFeatures, String testNameSuffix) {
		
		super(context);

		this.setName("testStandardTest");
		this.setTestMethodSuffix(testNameSuffix);

		this.typeName = typeName;
		this.maxFeatures = maxFeatures;
		this.context = context;

	}

	/**
	 * Sets up a test suite according to the CSV file.
	 * 
	 * @param context Test context
	 * @return the test suite
	 * @throws IOException Error reading config file
	 * @throws InvalidConfigFileException Error in config file
	 */
	static public Test suite(TestContext context) throws IOException, InvalidConfigFileException {

		// read CSV file
		String filename = (String) context.getEnvironment()
		.get(KEY_TESTS_CONFIG_DIR) + CONFIG_FILE;
		CSVReader csv = new CSVReader(new File(filename));      

		List<String[]> lines = csv.getLines();

		//if CSV file is empty
		if (lines==null) {
			throw new InvalidConfigFileException("File doesn't contain any data!");
		}

		// remove header
		lines.remove(0);
		
		//to store test parameters
		String[] types = new String[lines.size()];
		int[] maxFeatures = new int[lines.size()];

		//read test parameters
		for (int i=0; i<lines.size(); i++) {

			String[] line = lines.get(i);

			types[i] = line[0];
			maxFeatures[i] = new Integer(line[1]);

		}

		//create test suite
		TestSuite suite = new TestSuite();

		//add tests to suite
		for (int i=0; i<types.length; i++) {

			StandardOWS6Test test = 
				new StandardOWS6Test(context, types[i], maxFeatures[i], "#"+i);

			suite.addTest(test);
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
	 */
	public void initStandardTest(TestProperties properties) {
		
		//create simple request
		String body = this.createMaxFeaturesRequest(this.typeName, this.maxFeatures);

		this.data.put("body", body);

		properties.put(TestExecutor.KEY_REQUEST, body);

		properties.put(OWS6Keys.KEY_TYPE_NAME, this.typeName);
		properties.put(OWS6Keys.KEY_MAX_FEATURES, this.maxFeatures);
		properties.put(TestExecutor.KEY_DESCRIPTION,
				"Part of the standard test class. This is a test" +
				"with the typeName "+this.typeName + " and maxFeatures = "+ this.maxFeatures +".");
	}

	/**
	 * Runs the actual test:
	 * 
	 * Sends request to server.
	 * The response is stored in the context
	 * 
	 * @throws HttpException Problems with Http concetion
	 * @throws IOException Problems sending the request
	 */
	public void testStandardTest() throws HttpException, IOException {
		
		String response = comm.sendRequest(request, data);
		this.putCallProperty(TestExecutor.KEY_RESPONSE, response);
		
	}

	
	/**
	 * Checks and validates the response
	 */
	public void checkStandardTest() {
		
		//read received response
		String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE);

		try {

			//XML schema validation
			XMLSchemaValidator xsv = new XMLSchemaValidator();
			xsv.validate(response);
			
			//schematron validation
			this.validateSchematron(response);

		} catch (ConfigurationException e) {
			log.error("ConfigurationException during XML validation!", e);
		}
	}

	
	/**
     * Create a WFS request that requests a max number of features of
     * a certain type.
     * 
     * @param typeName Name of the feature to request
     * @param maxFeatures Number of maximum features
     * @return A WFS request
     */
    public String createMaxFeaturesRequest(String typeName, int maxFeatures) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature version=\"1.1.0\" xmlns:citygml=\"http://www.citygml.org/citygml/1/0/0\" " +
        		"xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        		"xsi:schemaLocation=\"http://www.opengis.net/wfs  http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\"");
        if (maxFeatures>0)
            request.append(" maxFeatures=\"" + maxFeatures + "\"");
        request.append(">");
        request.append(" <wfs:Query typeName=\"" + typeName + "\">");
        
        request.append("</wfs:Query></wfs:GetFeature>");
        return request.toString();
    }
}
