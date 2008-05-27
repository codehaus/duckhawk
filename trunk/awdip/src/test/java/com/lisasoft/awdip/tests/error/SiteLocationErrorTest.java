package com.lisasoft.awdip.tests.error;

import static com.lisasoft.awdip.AWDIPTestSupport.*;
import static org.custommonkey.xmlunit.XMLAssert.*;

import java.util.HashMap;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.ConformanceTest;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;

public class SiteLocationErrorTest extends ConformanceTest {

    static final String FEATURE_TYPE = "aw:SiteLocation";

    protected HashMap<String, String> data = new HashMap<String, String>();

    protected Communication comm;

    protected Request request;

    private String response;

    private XpathEngine xpath;

    public SiteLocationErrorTest() {
        super(getAwdipContext());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH);

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
        xpath = XMLUnit.newXpathEngine();
    }
    
    public void testNotXML() throws Exception {
        // init
        String body = "Hello there, can you please provide me with the SiteLocation data?";
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a plain text request.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        System.out.println(response);
        assertXpathExists(
                "/ows:ExceptionReport/ows:Exception/ows:ExceptionText",
                response);
    }
    
    public void testInvalidXML() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        // ... let's remove the closing elements 
        body = body.replaceAll("</wfs:GetFeature>", "");
        System.out.println(body);
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing feature type.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists(
                "/ows:ExceptionReport/ows:Exception/ows:ExceptionText",
                response);
    }
    
    
    public void testInvalidElementGetFeature() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        body = body.replaceAll("wfs:GetFeature", "wfs:InvalidElementHere");
        System.out.println(body);
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing feature type.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists(
                "/ows:ExceptionReport/ows:Exception/ows:ExceptionText",
                response);
        System.out.println(response);
        assertTrue(response.contains("InvalidElementHere"));
    }
    
    // THIS ONE NOW WAITS FOREVER BECAUSE THE SERVER ON THE OTHER SIDE IS NOT
    // CONFIGURED IN STRICT CITE MODE, MEANING THE VALIDATION IS NOT OCCURRING AND
    // THE FULL DATA SET IS BEING RETURNED
    public void testInvalidElementQuery() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        body = body.replaceAll("wfs:Query", "wfs:InvalidElementHere");
        System.out.println(body);
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing feature type.");
        
        // run
        // COMMENTED OUT TO AVOID WAITING FOREVER ON EACH REQUEST
//        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists(
                "/ows:ExceptionReport/ows:Exception/ows:ExceptionText",
                response);
        System.out.println(response);
        assertTrue(response.contains("InvalidElementHere"));
    }

    public void testWrongFeatureType() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocationNotThere");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing feature type.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists(
                "/ows:ExceptionReport/ows:Exception/ows:ExceptionText",
                response);
        assertTrue(response.contains("SiteLocationNotThere"));
    }
    
    public void testWrongNamespace() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("notthere:SiteLocation");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing namespace.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("notthere"));
    }
    
    public void testUnexistentAttributeFilter() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteLocation",
                0,
                Gml.createPropertyFilter("aw:theMissingProperty", "notThere"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing namespace.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("theMissingProperty"));
    }
    
    // THIS ONE FAILS FOR GOOD, IT SEEMS COMMUNITY SCHEMA IS UNABLE TO CHECK THE PROPERTY NAMES?
    public void testUnexistentAttributeProperty() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteLocation", 2);
        // force in an invalid property request
        body = body.replaceAll("</wfs:Query>", "<wfs:PropertyName>aw:theMissingProperty</wfs:PropertyName></wfs:Query>");
        System.out.println(body);
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing namespace.");
        
        // run
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
        
        // check
        System.out.println(response);
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("theMissingProperty"));
    }


}
