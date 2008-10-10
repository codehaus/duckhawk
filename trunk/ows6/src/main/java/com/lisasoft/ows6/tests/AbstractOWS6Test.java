package com.lisasoft.ows6.tests;


import static com.lisasoft.ows6.OWS6Keys.KEY_GS_PATH;
import static com.lisasoft.ows6.OWS6Keys.KEY_HOST;
import static com.lisasoft.ows6.OWS6Keys.KEY_PORT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestRunner;
import org.duckhawk.core.TestType;
import org.duckhawk.junit3.AbstractDuckHawkTest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;
import com.lisasoft.ows6.OWS6Keys;


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
	protected static Communication comm;

	
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
	 * validates against against a community schema
	 * @param the response that will be validated 
	 */
	public void validateSchema(String response)
	throws ConfigurationException, SAXException {

		InputSource is = new InputSource(new StringReader(response));
		Validator v = new Validator(is);
		v.useXMLSchema(true);
		//System.out.println(System.getProperty("user.dir"));
		//v.setJAXP12SchemaSource(new File((String)this.context
		//		.getEnvironment().get(KEY_SCHEMA_RPATH)));

		XMLAssert.assertXMLValid(v);

	}

	/** 
	 * validates against a set of schematron rules
	 * 
	 * @param the response that will be validated 
	 * @throws TransformerException 
	 * @throws IOException 
	 */
	public void validateSchematron(String response, String schematronFile, String transformerFile)
	throws ConfigurationException, SAXException, TransformerException, IOException {
		
		//load schematron rules
		File rules = new File(schematronFile);
		//load schematron transformer
	    File skeleton = new File(transformerFile);
		//Stream to buffer the transformed rules (XSLT)
		OutputStream pipe = new ByteArrayOutputStream();
		//results of the schematron validation
		OutputStream resultStream = new ByteArrayOutputStream();
	    Result result = new StreamResult(resultStream);
	    
	    //create xslt
	    transformXSLTFiles(rules, skeleton, pipe);
		
		//apply the transformed rules
		javax.xml.transform.Source rulestream =
	        new javax.xml.transform.stream.StreamSource
	        (new ByteArrayInputStream(((ByteArrayOutputStream)pipe).toByteArray()));
	    transformXSLTStreams(new StreamSource(new StringReader(response)), rulestream, result);
	    
	    //Storing the results and related stuff
	    this.putCallProperty(OWS6Keys.KEY_SCHEMATRON_XLST, ((ByteArrayOutputStream)pipe).toString("UTF-8"));
	    
	    String schematronResult = ((ByteArrayOutputStream)resultStream).toString("UTF-8");
	    log.info("Schematron result:\n"+schematronResult);
	    this.putCallProperty(OWS6Keys.KEY_SCHEMATRON_RESPONSE, schematronResult);
	    
	    assertTrue(EMPTY_SCHEMATRON_RESULT.equals(schematronResult));
	}
	
	
	/**
	 * Transforms schematron rules (or any other xml file) according to the
	 * given xslt file
	 * 
	 * @param rules the schematron rules as xml
	 * @param xsltFile the xslt file transforming the rules
	 * @param output receives the transformed rules
	 * @throws TransformerException if something goes wrong during the transformation
	 */
	private void transformXSLTFiles(File rules, File xsltFile, OutputStream output) throws TransformerException {
		javax.xml.transform.Source xmlSource =
	        new javax.xml.transform.stream.StreamSource(rules);
	    javax.xml.transform.Source xsltSource =
	        new javax.xml.transform.stream.StreamSource(xsltFile);
	    javax.xml.transform.Result result =
	        new javax.xml.transform.stream.StreamResult(output);
			
		transformXSLTStreams(xmlSource, xsltSource, result);
	}
	
	
	/**
	 * Transforms a streamsource containing xml according to given
	 * xslt (also in a stream source).
	 * 
	 * This function is used for schematron validation.
	 * 
	 * @param xml the xml that will be transformed
	 * @param xslt the xslt file transforming the xml
	 * @param result receives the result of the transformation
	 * @throws TransformerException if something goes wrong during the transformation
	 */
	private void transformXSLTStreams(Source xml, Source xslt, Result result) throws TransformerException {
		
		// create an instance of TransformerFactory
	    javax.xml.transform.TransformerFactory transFact =
	        javax.xml.transform.TransformerFactory.newInstance( );
			
	    javax.xml.transform.Transformer trans =
	        transFact.newTransformer(xslt);
	 
	    //transform xml
	    trans.transform(xml, result);
		
	}

}
