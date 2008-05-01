package com.lisasoft.awdip.tests.conformance;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.duckhawk.junit3.ConformanceTest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.lisasoft.awdip.util.Communication;

public class XercesJaxpTest extends ConformanceTest {
	

    public XercesJaxpTest() {
        super(getAwdipContext());
    }
    
    protected void setUp() {
    	System.out.println("XercesJaxpTest");
    }
    
    public void testJAXPValidation() throws IOException, ParserConfigurationException, SAXException {
    	
    	String response = this.sendRequest();
    	
    	System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	factory.setNamespaceAware(true);
    	factory.setValidating(true);
    	factory.setAttribute(
    		    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
    		    "http://www.w3.org/2001/XMLSchema");
    	factory.setAttribute(
    		    "http://java.sun.com/xml/jaxp/properties/schemaSource",
    		    getTestProperty(KEY_SCHEMA_RPATH));
   		
			DocumentBuilder builder = factory.newDocumentBuilder();
			Validator handler=new Validator();
			builder.setErrorHandler(handler); 
			
			InputSource is = new InputSource(new StringReader(response));
			builder.parse(is);
			
			assertTrue(handler.getErrors(), handler.isValid());	
    }
       
	private class Validator extends DefaultHandler {
		
		private boolean validationError = false;  
		private SAXParseException saxParseException = null;
		private StringBuffer sb = new StringBuffer();
		

		public void error(SAXParseException exception)
		throws SAXException {
			sb.append("Error: "+exception);
			validationError = true;
			saxParseException = exception;
		}   

		public void fatalError(SAXParseException exception)
		throws SAXException {
			sb.append("Fatal Error: "+exception);
			validationError = true;	    
			saxParseException=exception;	     
		}
		
		public boolean isValid() {
			return !validationError;
		}
		
		public String getErrors() {
			return sb.toString();
		}
	}
    
	private String sendRequest() throws IOException {
		String replacement = "xsi:schemaLocation=\"http://www.water.gov.au/awdip https://www.seegrid.csiro.au/subversion/xmml/" +
				"AWDIP/trunk/geoserver_conf/commonSchemas/awdip.xsd http://www.opengis.net/wfs http://schemas.opengis.net/wfs/" +
				"1.1.0/wfs.xsd\"";
		String regex = "xsi:schemaLocation=\".*xsd\"";

		String body = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" " +
		"xmlns:wfs=\"http://www.opengis.net/wfs\"   xmlns:ogc=\"http://www.opengis.net/ogc\" " +
		"xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" "+ 
		"xmlns:aw=\"http://www.water.gov.au/awdip\" xmlns:ows=\"http://www.opengis.net/ows\" "+
		"xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "+

		"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs " +
		"http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\" maxFeatures=\"5\"> " +
		"<wfs:Query typeName=\"aw:SiteLocation\"> </wfs:Query> </wfs:GetFeature>";		

		String host = (String) getTestProperty(KEY_HOST);
		int port = (Integer) getTestProperty(KEY_PORT);
		String path = (String) getTestProperty(KEY_GS_PATH) + "/wfs";
		String response = Communication.sendWFSPost(host, port, path, body);
		
		return response.replaceAll(regex, replacement);
	}
}
