package com.lisasoft.awdip.tests.performance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


import org.apache.commons.httpclient.HttpException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestListener;
import org.duckhawk.junit3.PerformanceTest;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;



public class SiteLocationPerfTest extends PerformanceTest  {
    static Communication comm;
    
    String host = "thor3.adl.ardec.com.au";
    int port = 5580;
    String geoserverLocation = "geoserver";
    

    /** data sent to the server (path and body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();

    /** Request sent to the server */
    Request request;
    
    String response = "";

    
    /** Bounding box to start with, and size changes for the tests **/
    //double[] bbox = new double[]{125.7, -19.3, 128.7, -16.3};
    double[] bbox;

    /**  size changes of the bounding box for the tests
     *  key = ID for test
     *  1. value grow along latitude (in both directions)
     *  2. value grow along longitude (in both directions)
     */
    HashMap<String,double[]> bboxGrow;
    

    public SiteLocationPerfTest() {
        super("WfsTest", "1.0", 5, getListeners());

    }
    
    
    @Override
    protected void setUp() throws Exception {
        // TODO vmische when global properties per Testsuite are available,
        //      put nice descriptions about each test in   
        
        comm = new Communication(host, port);

        request = new Request(RequestMethod.POST,
                "/" + geoserverLocation + "/TestWfsPost");

        data.put("url", "http://" + host + ":" + port + "/"
                + geoserverLocation + "/wfs");
        
        bbox = new double[]{127.2, -17.9, 127.3, -17.8};

        /** size changes of the bounding box for the tests
         * key = ID for test
         *  1. value grow along latitude (in both directions)
         *  2. value grow along longitude (in both directions)
         */
        bboxGrow = new HashMap<String, double[]>();
        bboxGrow.put("bb10000",  new double[]{0.5, 0.5});
        bboxGrow.put("bb40000",  new double[]{1.0, 1.0});
        bboxGrow.put("bb90000",  new double[]{1.5, 1.5});
        bboxGrow.put("bb160000",  new double[]{2.0, 2.0});
        bboxGrow.put("bb250000", new double[]{2.5, 2.5});
        bboxGrow.put("bb360000", new double[]{3.0, 3.0});
        bboxGrow.put("bb490000", new double[]{3.5, 3.5});
        bboxGrow.put("bb640000", new double[]{4.0, 4.0});
        bboxGrow.put("bb810000", new double[]{4.5, 4.5});
        bboxGrow.put("bb1000000", new double[]{5.0, 5.0});
        bboxGrow.put("bb1440000", new double[]{6.0, 6.0});
        bboxGrow.put("bb1960000", new double[]{7.0, 7.0});
        bboxGrow.put("bb2560000", new double[]{8.0, 8.0});
        bboxGrow.put("bb3240000", new double[]{9.0, 9.0});
        bboxGrow.put("bb4000000", new double[]{10.0, 10.0});
    }

    
    
    static TestListener[] listeners;
    
    private static TestListener[] getListeners() {
        if (listeners == null) {
            listeners = new TestListener[] {
                    new PerformanceSummarizer(), //
                    new PrintStreamListener(true, true), // 
                    new XStreamDumper(new File("./target/dh-report"))
            };
        }
        return listeners;
    }
    
    /** Creates the body for a request with a bounding box filter
     * 
     * @param bbox the bounding box that should be used
     * @return
     */
    private String createBoundingBoxRequest(double[] bbox) {
        String request = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\" maxFeatures=\"500\">"
            +" <wfs:Query typeName=\"aw:SiteLocation\">"
            + "<ogc:Filter><ogc:BBOX><ogc:PropertyName>sa:position</ogc:PropertyName><gml:Envelope srsName=\"EPSG:4326\">"
            + "<gml:lowerCorner>"+bbox[0]+" "+bbox[1]+"</gml:lowerCorner>"
            + "<gml:upperCorner>"+bbox[2]+" "+bbox[3]+"</gml:upperCorner>"
            + "</gml:Envelope></ogc:BBOX></ogc:Filter>"
            +"</wfs:Query></wfs:GetFeature>";
        
        return request;
    }
    
    
    /** Creates a new bounding box, based on the given key. The values for the
     * key are retrieved from  @see bbGrow. The base bounding box is @see bbox.
     * @param key desired bbGrow offsets 
     * @return new bounding box
     */
    private double[] getGrownBbox(String key) {
        double[] offsets = bboxGrow.get(key);
        return new double[]{
                (bbox[0]-offsets[0]), (bbox[1]-offsets[1]),   
                (bbox[2]+offsets[0]), (bbox[3]+offsets[1]),   
        };
    }
    
  
    public void testSiteLocationBoundingBox100()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(bbox);
        
        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);
        
        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }
   
    public void testSiteLocationBoundingBox10000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb10000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox40000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb40000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationBoundingBox90000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb90000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox160000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb160000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox250000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb250000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox360000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb360000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox490000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb490000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox640000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb600000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb810000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox100000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb100000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb1440000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb1960000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void testSiteLocationBoundingBox2560000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb2560000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void testSiteLocationBoundingBox3240000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb3240000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void testSiteLocationBoundingBox4000000()
            throws HttpException, IOException {
        String body = createBoundingBoxRequest(getGrownBbox("bb4000000"));

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }
}

