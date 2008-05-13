package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_DESCRIPTION;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.PerformanceTest;
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
public class SiteSinglePhenomTimeSeriesBetweenDates extends PerformanceTest {
    static Communication comm;

    /** data sent to the server (body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    Request request;
    
    String response = "";
    
    public SiteSinglePhenomTimeSeriesBetweenDates() {
        super(getAwdipContext(), 5);
        putEnvironment(KEY_DESCRIPTION,
                "This test tests the impact of restricting the returned data" +
                "to a certain period (days/month). It tests one phenomenon" +
                "type (PREC_TotD and PREC_TotM) between several dates. Don't" +
                "interpret to much into this test, as you never know how many" +
                "features get returned");
    }
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH) + "/wfs";

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
    }


    private String createOnePhenomenonTypeFilter(String phenomenonType) {
        return "<ogc:PropertyIsEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name</ogc:PropertyName><ogc:Literal>"
        + phenomenonType + "</ogc:Literal></ogc:PropertyIsEqualTo>";
    }

    private String createBetweenTwoDatesFilter(String from, String to) {
        return "<ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry</ogc:PropertyName><ogc:Literal>"
        + from + "</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry</ogc:PropertyName><ogc:Literal>"
        + to + "</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo>";        
    }




/*    
    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates2(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(2 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-02"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates2()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates2()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    } 

    
    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates3(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(3 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-03"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates3()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates3()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    } 
    
    
    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates4(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(4 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-04"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates4()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates4()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates5(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(5 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-05"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates5()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates5()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
    
    
    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates6(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(6 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-06"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates6()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates6()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }         

    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates7(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(7 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-07"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates7()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates7()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }         

    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates8(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(8 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-08"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates8()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates8()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }         

    public void initOnePhenomenonTypePREC_TotDBetweenTwoDates9(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotD) between two dates " +
                "(9 days).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotD"),
                createBetweenTwoDatesFilter("2008-01-01", "2008-01-09"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDBetweenTwoDates9()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDBetweenTwoDates9()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }         
*/


    

    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates2(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(2 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-02-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates2()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates2()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates3(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(3 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-03-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates3()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates3()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates4(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(4 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-04-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates4()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates4()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates5(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(5 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-05-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates5()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates5()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates6(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(6 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-06-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates6()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates6()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates7(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(7 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-07-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates7()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates7()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates8(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(8 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-08-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates8()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates8()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates9(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(9 monthsn).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-09-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates9()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates9()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates10(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(10 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-02-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates10()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates10()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
 
    public void initOnePhenomenonTypePREC_TotMBetweenTwoDates11(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomena (PREC_TotM) between two dates " +
                "(11 months).");
                    
        String body = Gml.createAndFilterRequest(
                "aw:SiteSinglePhenomTimeSeries",
                createOnePhenomenonTypeFilter("PREC_TotM"),
                createBetweenTwoDatesFilter("2007-01-01", "2007-11-01"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMBetweenTwoDates11()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMBetweenTwoDates11()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) > 0",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }             
}
