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
import com.lisasoft.awdip.util.Util;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * Tests the conformance of the SiteSinglePhenomTimeSeriesFeatures
 * 
 * @author vmische
 *
 */
public class SiteLocation extends ConformanceTest {
    static Communication comm;

    /** data sent to the server (body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    Request request;
    
    String response = "";
    
    /** Date range for the requests */
    String[] dateRange = new String[2];
    
    /** properties that should make it into the output */
    static final String KEY_BBOX = "params.boundingBox";;
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX
    }; 
    
    Random random = new Random();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    // set bounding box range. Bounding box coordinate order:
    // lower left x/y, lower right x/y
    double[] minBbox = new double[]{127, -17.9, 127.3, -17.8};
    //double[] maxBbox = new double[]{126, -19, 128, -17};
    double[] maxBbox = new double[]{122, -23, 132, -13};
    
    
    public SiteLocation() {
        super(getAwdipContext(forcePropertyOutput));
        putEnvironment(KEY_DESCRIPTION,
                "Request all features of type SiteLocation within a randomly " +
                "(within a certain range) chosen bounding box. Maximum " +
                "number of returned features is 1000");
        random.setSeed(100);
    }
    
    /** Constructor for reliability aggregator
     * 
     * @param dummy Dummy parameter to make distinction from default constructor
     */ 
    public SiteLocation(Object dummy) {
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



    /**
     * Returns a 4-tuple with a randomly chosen bounding box within a given
     * range
     * 
     * @param rangeStart Beginning of the range
     * @param rangeEnd End of the range
     */
    private double[] randomBoundingBox(double[] minBbox, double[] maxBbox) {
        double[] bbox = new double[4];
        double interval;
        
        for (int i=0; i<bbox.length; i++) {
            interval = maxBbox[i] - minBbox[i];
            bbox[i] = minBbox[i] + (interval*random.nextFloat());
        }
        
        // ensure min values are bigger than max values
        if ((bbox[0]>0 && bbox[2]>0 && bbox[0] > bbox[2])
                || (bbox[0]<0 && bbox[2]<0 && bbox[0] < bbox[2]))
            Util.swapDouble(bbox,0,2);
        if ((bbox[1]>0 && bbox[3]>0 && bbox[1] < bbox[3])
                || (bbox[1]<0 && bbox[3]<0 && bbox[1] > bbox[3]))
            Util.swapDouble(bbox,1,3);
        
        return bbox;
    }
 
    /**
     * Creates an XPath query that returns how many locations are not within
     * a specified bounding box
     * 
     * @param bbox bounding within the location should be
     * @return XPath expression that will the number of locations that are not
     *     within the specified bounding box 
     */
    private String createCountNotWithinBoundingBoxXpath(double[] bbox) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteLocation/sa:position/gml:Point/gml:pos[" +
        "number(substring-before(.,' ')) < " + (bbox[0]<bbox[2]?bbox[0]:bbox[2]) + 
        " or number(substring-before(.,' ')) > " + (bbox[0]<bbox[2]?bbox[2]:bbox[0]) + 
        " or number(substring-after(.,' ')) < " + (bbox[1]<bbox[3]?bbox[1]:bbox[3]) + 
        " or number(substring-after(.,' ')) > " + (bbox[1]<bbox[3]?bbox[3]:bbox[1]) + "])";
    }
    
    

    /**
     * Make random aw:SiteSinglePhenomTimeSeries request with a certain
     * phenomenon type within a certain time range 
     * @throws ParseException 
     */
    private void mainTest(String featureType, double[] minBbox,
            double[] maxBbox) throws HttpException, IOException, ParseException {
        // create a new bounding box within range
        double[] bbox = randomBoundingBox(minBbox, maxBbox);
        
        putCallProperty(KEY_BBOX, bbox);
        
        // can't be done on init, as values should change on every call
        String body = Gml.createAndFilterMaxFeaturesRequest(
                "aw:SiteLocation",
                10,
                Gml.createBoundingBoxFilter(bbox));
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        
        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }
    
    /**
     * XMLunit test this test. It's the same for all tests.
     */
    private void checkTest() throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "count(/wfs:FeatureCollection/gml:featureMembers) <= 1000",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));

        // make sure that there were no features outside of the bounding box
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountNotWithinBoundingBoxXpath(
                        (double[])getCallProperty(KEY_BBOX)),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
    }
    
    
    
    public void initVariableBoundingBox(TestProperties props) {
        props.put(TestExecutor.KEY_DESCRIPTION,
                "Request all features of type SiteLocation within a randomly " +
                "(within a certain range) chosen bounding box. Maximum " +
                "number of returned features is 1000");
    }
    public void testVariableBoundingBox()
    throws HttpException, IOException, ParseException, InterruptedException {
           mainTest("aw:SiteLocation", minBbox, maxBbox);
    }
    public void checkVariableBoundingBox()
    throws XpathException, SAXException, IOException {
        checkTest();
    }    
}
