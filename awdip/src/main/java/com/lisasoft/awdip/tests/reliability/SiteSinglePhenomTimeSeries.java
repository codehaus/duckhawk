package com.lisasoft.awdip.tests.reliability;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_DESCRIPTION;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.ConformanceTest;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * Tests the conformance of the SiteSinglePhenomTimeSeriesFeatures
 * 
 * @author vmische
 *
 */
public class SiteSinglePhenomTimeSeries extends ConformanceTest {
    static Communication comm;

    /** data sent to the server (body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    Request request;
    
    String response = "";
    
    /** Date range for the requests */
    String[] dateRange = new String[2];
    
    /** properties that should make it into the output */
    static final String KEY_DATE_START = "params.dateStart";
    static final String KEY_DATE_END = "params.dateEnd";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_DATE_START,
            KEY_DATE_END
    }; 
    
    
    Random rand = new Random();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    
    public SiteSinglePhenomTimeSeries() {
        super(getAwdipContext(forcePropertyOutput));
        putEnvironment(KEY_DESCRIPTION,
                "Tests the singlePhenomTimeSeries feature type.");
    }
    
    /** Constructor for reliability aggregator
     * 
     * @param dummy Dummy parameter to make distinction from default constructor
     */ 
    public SiteSinglePhenomTimeSeries(Object dummy) {
        this();
        setUp();
    }   
    
    
    @Override
    protected void setUp() {
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


    /**
     * Returns a 2-tuple with a randomly choosen date range within a given range
     * 
     * @param rangeStart Beginning of the range
     * @param rangeEnd End of the range
     */
    private String[] randomDatePair(Date rangeStart, Date rangeEnd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long rangeStartLong = rangeStart.getTime();
        long interval = rangeEnd.getTime()-rangeStartLong;
        long startDateLong =rangeStartLong+(long)(interval*rand.nextFloat());
        long endDateLong = rangeStartLong+(long)(interval*rand.nextFloat());
        
        return (startDateLong < endDateLong)
                ? new String[]{format.format(new Date(startDateLong)),
                        format.format(new Date(endDateLong))}
                : new String[]{format.format(new Date(endDateLong)),
                        format.format(new Date(startDateLong))};
    }
 
    /**
     * Creates an XPath query for counting the appearance of dates on, before
     * or after a certain date. It selects date values (yyyy-MM-dd or format)
     * with XPath and compare it to an asserted date.
     * It will perform: datesReturnedFromXPath operator date
     * Write the operator as e.g. ">" and *not* "&gt;" 
     * 
     * @param date Date the values selected by the XPath query should be
     *        compared to (in format: yyyy-MM-dd or yyyMMdd)
     * @param operator Operator to compare the dates
     * @return XPath expression to get the count of these dates
     */
    private String createCountDatesXpath(String operator, String date) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteSinglePhenomTimeSeries/aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry[translate(substring(.,1,10),'-','') " +
    		operator + " translate('" +
    		date + "','-','') ])";
    }
    
    
    /**
     * XMLunit test this test. It's the same for all tests.
     */
    private void checkTest() throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 500",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));

        // make sure that there were no features outside of the date range
        // returned
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        "<",
                        (String)getCallProperty(KEY_DATE_START)),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        ">",
                        (String)getCallProperty(KEY_DATE_END)),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
    }
    
    /**
     * Make random aw:SiteSinglePhenomTimeSeries request with a certain
     * phenomenon type within a certain time range 
     * @throws ParseException 
     */
    private void mainTest(String phenomType, String startDate,
            String endDate) throws HttpException, IOException, ParseException {
        // create a new date range
        dateRange = randomDatePair(df.parse(startDate), df.parse(endDate));
        putCallProperty(KEY_DATE_START, dateRange[0]);
        putCallProperty(KEY_DATE_END, dateRange[1]);
        
        // can't be done on init, as values should change on every call
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteSinglePhenomTimeSeries",
                500,
                createOnePhenomenonTypeFilter(phenomType),
                createBetweenTwoDatesFilter(dateRange[0], dateRange[1]));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }
    
    
    
    
    public void initOnePhenomenonTypePREC_ToDBetweenTwoDates(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Reqeust one type of phenomena (PREC_ToD) within two randomly" +
                "choosen dates. Maximum of returned features is 500");
    }
    public void testOnePhenomenonTypePREC_ToDBetweenTwoDates()
    throws HttpException, IOException, ParseException {
           mainTest("PREC_TotD","2008-01-01", "2008-01-10");
    }
    public void checkOnePhenomenonTypePREC_ToDBetweenTwoDates()
    throws XpathException, SAXException, IOException {
        checkTest();
    }    
    
    public void initOnePhenomenonTypePREC_ToMBetweenTwoDates(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Reqeust one type of phenomena (PREC_ToM) within two randomly" +
                "choosen dates. Maximum of returned features is 500");
    }
    public void testOnePhenomenonTypePREC_ToMBetweenTwoDates()
    throws HttpException, IOException, ParseException {
           mainTest("PREC_TotM","2007-01-01", "2007-11-01");
    }
    public void checkOnePhenomenonTypePREC_ToMBetweenTwoDates()
    throws XpathException, SAXException, IOException {
        checkTest();
    }
    

    public void initOnePhenomenonTypePREC_ToYBetweenTwoDates(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Reqeust one type of phenomena (PREC_ToY) within two randomly" +
                "choosen dates. Maximum of returned features is 500");
    }
    public void testOnePhenomenonTypePREC_ToYBetweenTwoDates()
    throws HttpException, IOException, ParseException {
           mainTest("PREC_TotY","1890-01-01", "2007-01-01");
    }
    public void checkOnePhenomenonTypePREC_ToYBetweenTwoDates()
    throws XpathException, SAXException, IOException {
        checkTest();
    }
}
