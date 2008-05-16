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
public class SiteSinglePhenomTimeSeriesMaximumFeatures extends StressTest {
    static Communication comm;

    /** data sent to the server (body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    Request request;
    
    String response = "";
    
    
    /** Use as performance test with one thread */
    public SiteSinglePhenomTimeSeriesMaximumFeatures() {
        this(getPerfTimes(), 1, 0);
    }

    /** For load tests with multiple threads */
    public SiteSinglePhenomTimeSeriesMaximumFeatures(int times, int numThreads,
            int rampUp) {
        super(getAwdipContext(), times, numThreads, rampUp);
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
        String path = (String) getEnvironment(KEY_GS_PATH);

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
    }


    private String createOnePhenomenonTypeFilter(String phenomenonType) {
        return "<ogc:PropertyIsEqualTo><ogc:PropertyName>aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name</ogc:PropertyName><ogc:Literal>"
        + phenomenonType + "</ogc:Literal></ogc:PropertyIsEqualTo>";
    }
    
 
    

    public void initOnePhenomenonTypePREC_TotDAnyDate200(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 100 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                200,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_DotDAnyDate200()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate200()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 200",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotDAnyDate400(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 400 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                400,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate400()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate400()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 400",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate600(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 600 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                600,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate600()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate600()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 600",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate800(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 800 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                800,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate800()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate800()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 800",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate1000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 1000 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1000,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate1000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate1000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate1200(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 1200 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1200,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate1200()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate1200()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1200",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate1400(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 1400 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1400,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate1400()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate1400()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1400",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate1600(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 1600 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1600,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate1600()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate1600()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1600",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    public void initOnePhenomenonTypePREC_TotDAnyDate1800(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
            "Test one type of phenomenon (PREC_TotD) at any date with a" +
            "maximum of 1800 features (total features are 1885)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1800,
                createOnePhenomenonTypeFilter("PREC_TotD"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotDAnyDate1800()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotDAnyDate1800()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1800",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
    
    
    
    
    public void initOnePhenomenonTypePREC_TotMAnyDate10(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 10 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                10,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate10()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate10()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 10",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate20(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 20 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                20,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate20()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate20()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 20",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate30(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 30 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                30,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate30()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate30()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 30",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate40(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 40 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                40,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate40()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate40()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 40",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate50(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 50 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                50,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate50()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate50()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 50",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate60(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 60 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                60,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate60()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate60()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 60",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate70(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 70 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                70,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate70()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate70()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 70",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate80(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 80 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                80,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate80()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate80()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 80",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate90(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 90 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                90,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate90()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate90()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 90",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate100(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 100 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                100,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate100()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate100()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 100",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    public void initOnePhenomenonTypePREC_TotMAnyDate110(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 110 features (total features are 114)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                110,
                createOnePhenomenonTypeFilter("PREC_TotM"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotMAnyDate110()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotMAnyDate110()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 110",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
    
    
    
    public void initOnePhenomenonTypePREC_TotYAnyDate1000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 1000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                1000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate1000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate1000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate2000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 2000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                2000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate2000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate2000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 2000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate3000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 3000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                3000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate3000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate3000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 3000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate4000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 4000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                4000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate4000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate4000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 4000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate5000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 5000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                5000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate5000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate5000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 5000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    
/*
    public void initOnePhenomenonTypePREC_TotYAnyDate6000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 6000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                6000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate6000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate6000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 6000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate7000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 7000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                7000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate7000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate7000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 7000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate8000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 8000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                8000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate8000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate8000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 8000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate9000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 9000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                9000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate9000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate9000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 9000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate10000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 10000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                10000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate10000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate10000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 10000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate11000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 11000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                11000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate11000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate11000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 11000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate12000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 1000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                12000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate12000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate12000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 12000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }    

    public void initOnePhenomenonTypePREC_TotYAnyDate13000(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Test one type of phenomenon (PREC_TotM) at any date with a" +
                "maximum of 13000 features (total features are 13395)");

        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                13000,
                createOnePhenomenonTypeFilter("PREC_TotY"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    public void testOnePhenomenonTypePREC_TotYAnyDate13000()
    throws HttpException, IOException {
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);  
    }
    public void checkOnePhenomenonTypePREC_TotYAnyDate13000()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 13000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    */
}
