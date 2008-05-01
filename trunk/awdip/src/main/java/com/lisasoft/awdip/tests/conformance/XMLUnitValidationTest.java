package com.lisasoft.awdip.tests.conformance;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.duckhawk.junit3.ConformanceTest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import com.lisasoft.awdip.util.Communication;

public class XMLUnitValidationTest  extends ConformanceTest {
	
	public XMLUnitValidationTest() {
		super(getAwdipContext());
	}

	protected void setUp() {
	}

	public void testValidateResponse() throws ConfigurationException, SAXException, IOException {

		String response = this.sendRequest();

		InputSource is = new InputSource(new StringReader(response));
		Validator v = new Validator(is);
		v.useXMLSchema(true);
		v.setJAXP12SchemaSource(new File((String) getTestProperty(KEY_SCHEMA_RPATH)));
		
		assertTrue(v.toString(), v.isValid());
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
