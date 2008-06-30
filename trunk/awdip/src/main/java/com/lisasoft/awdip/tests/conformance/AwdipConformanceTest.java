package com.lisasoft.awdip.tests.conformance;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_SCHEMA_RPATH;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XpathException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class AwdipConformanceTest {

    /** validates against the AWDIP community schema
     * @param the response that will be validated 
     */
    public static void validate(String response)
    throws ConfigurationException, SAXException {
        String replacement = "xsi:schemaLocation=\"http://www.water.gov.au/awdip https://www.seegrid.csiro.au/subversion/xmml/" +
        "AWDIP/trunk/geoserver_conf/commonSchemas/awdip.xsd http://www.opengis.net/wfs http://schemas.opengis.net/wfs/" +
        "1.1.0/wfs.xsd\"";
        String regex = "xsi:schemaLocation=\".*xsd\"";        
        response = response.replaceAll(regex, replacement);
        
        InputSource is = new InputSource(new StringReader(response));
        Validator v = new Validator(is);
        v.useXMLSchema(true);
        System.out.println(System.getProperty("user.dir"));
        v.setJAXP12SchemaSource(new File((String)getAwdipContext()
                .getEnvironment().get(KEY_SCHEMA_RPATH)));
        
        XMLAssert.assertXMLValid(v);
        //XMLUnit. assertTrue(v.toString(), v.isValid());
    }
    
    /**
     * Checks if the numberOfFeatures attribute really equals the returned
     * number of features (count child nodes of <gml:featureMembers>)
     * @throws IOException 
     * @throws SAXException 
     * @throws XpathException 
     */
    public static void NumberOfFeaturesCheck(String response)
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathValuesEqual("number(/wfs:FeatureCollection/@numberOfFeatures)",
                "count(/wfs:FeatureCollection/gml:featureMembers/*)",
                response);
    }
}
