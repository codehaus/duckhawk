package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

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

import com.lisasoft.awdip.AWDIPTestSupport;
import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * Test with single property filter and growing bounding box
 * 
 * Rational: how big is the performance hit if a property filter is added. The
 * bounding box grows at in the @see SiteLocationPerTestBoundingBox test.
 * 
 * @author vmische
 *
 */
public class SiteLocationPerfTestPropertyAndBoundingBox extends PerformanceTest  {
    static Communication comm;
    
    /** data sent to the server (path and body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();

    /** Request sent to the server */
    Request request;
    
    String response = "";

    
    /** Bounding box to start with, and size changes for the tests **/
    double[] bboxInit;

    /**  size changes of the bounding box for the tests
     *  key = ID for test
     *  1. value grow along latitude (in both directions)
     *  2. value grow along longitude (in both directions)
     */
    HashMap<String,double[]> bboxGrow;
    

    public SiteLocationPerfTestPropertyAndBoundingBox() {
        super(AWDIPTestSupport.getAwdipContext(), 5);

    }
    
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getTestProperty(KEY_HOST);
        int port = (Integer) getTestProperty(KEY_PORT);
        String path = (String) getTestProperty(KEY_GS_PATH) + "/wfs";
        comm = new Communication(host, port);

        request = new Request(RequestMethod.POST,
                "/" + path + "/TestWfsPost");

        data.put("url", "http://" + host + ":" + port + "/"
                + path + "/wfs");
        
        bboxInit = new double[]{127.2, -17.9, 127.3, -17.8};

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




    
    /** Creates a new bounding box, based on the given key. The values for the
     * key are retrieved from  @see bbGrow. The base bounding box is @see bbox.
     * @param key desired bbGrow offsets 
     * @return new bounding box
     */
    private double[] getGrownBbox(String key) {
        double[] offsets = bboxGrow.get(key);
        return new double[]{
                (bboxInit[0]-offsets[0]), (bboxInit[1]-offsets[1]),   
                (bboxInit[2]+offsets[0]), (bboxInit[3]+offsets[1]),   
        };
    }
    

    public void testSiteLocationBoundingBox100()
    throws HttpException, IOException {
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bboxInit),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bboxInit);
        putCallProperty("boundingBoxSize", "100");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox10000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb10000");        
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));        

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "10000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox40000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb40000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "40000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationBoundingBox90000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb90000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "90000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox160000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb160000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "160000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox250000()
    throws HttpException, IOException {
       double[] bbox = getGrownBbox("bb250000");                
       String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

       putCallProperty("boundingBox", bbox);
       putCallProperty("boundingBoxSize", "250000");
       
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox360000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb360000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "360000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox490000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb490000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "490000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox640000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "640000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "810000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1000000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "1000000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "1440000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "1960000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "2560000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");                
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "3240000");
        
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body =  Gml.createAndFilterRequest("aw:SiteLocation",
                Gml.createBoundingBoxFilter(bbox),
                Gml.createPropertyFilter("aw:samplingRegimeType", "monitoring"));

        putCallProperty("boundingBox", bbox);
        putCallProperty("boundingBoxSize", "4000000");
                
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
}

