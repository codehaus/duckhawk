package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_DESCRIPTION;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.StressTest;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * Tests the performance of the SiteSinglePhenomTimeSeriesFeatures
 * 
 * @author vmische
 *
 */
public class SiteSinglePhenomTimeSeries extends StressTest {
    static Communication comm;

    /** data sent to the server (body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    Request request;
    
    String response = "";
    
    /** Use as performance test with one thread */
    public SiteSinglePhenomTimeSeries() {
        this(getPerfTimes(), 1, 0);
    }
    
    
    /** For load tests with multiple threads */
    public SiteSinglePhenomTimeSeries(int times, int numThreads,
            int rampUp) {
        super(getAwdipContext(), times, numThreads, rampUp);
        putEnvironment(KEY_DESCRIPTION,
                "Tests the singlePhenomTimeSeries feature type.");
    }          
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH);

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
    }


    private String createOnePhenomenonFilter(String phenomenonId) {
        return "<ogc:PropertyIsEqualTo><ogc:PropertyName>gml:name</ogc:PropertyName><ogc:Literal>"
        + phenomenonId + "</ogc:Literal></ogc:PropertyIsEqualTo>";
    }
    
    private String createOnePhenomenonTypeFilter(String phenomenonType) {
        return "<ogc:PropertyIsEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name</ogc:PropertyName><ogc:Literal>"
        + phenomenonType + "</ogc:Literal></ogc:PropertyIsEqualTo>";
    }
    
    private String createOneDateFilter(String date) {
        return "<ogc:PropertyIsEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry</ogc:PropertyName><ogc:Literal>"
        + date + 
        "</ogc:Literal></ogc:PropertyIsEqualTo>";
    }

    private String createBetweenTwoDatesFilter(String from, String to) {
        return "<ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry</ogc:PropertyName><ogc:Literal>"
        + from + "</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry</ogc:PropertyName><ogc:Literal>"
        + to + "</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo>";        
    }



            
    public void initOnePhenomenonAnyDate(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one phenomenon at any date");
        
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonFilter("1006"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonAnyDate()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonAnyDate()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("1",
                "/wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    
    
    public void initOnePhenomenonOneDate(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one phenomenon at one date");

        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonFilter("1006"),
                createOneDateFilter("2008-01-05"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonOneDate()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonOneDate()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("1",
                "/wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    
    public void initOnePhenomenonBetweenTwoDates(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one phenomenon between two dates");

        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonFilter("1006"),
                createBetweenTwoDatesFilter("2008-01-04", "2008-01-06"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonBetweenTwoDates()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonBetweenTwoDates()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("1",
                "/wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }

    

    public void initOnePhenomenonTypeAnyDate50(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomena at any date with a maximum of" +
            "50 features (else data would be too much).");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                50,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypeAnyDate50()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypeAnyDate50()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 50",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void initOnePhenomenonTypeOneDate50(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomena at one date with a maximum of" +
            "50 features (else data would be too much).");
        
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                50,
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createOneDateFilter("2007-03-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypeOneDate50()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypeOneDate50()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 50",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    
    public void initOnePhenomenonTypeBetweenTwoDates50(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena between two datse with a" +
                "maximum of 50 features (else data would be too much).");
                    
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                50,
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-23", "2007-03-21"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypeBetweenTwoDates50()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypeBetweenTwoDates50()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 50",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }      
}
