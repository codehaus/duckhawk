package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.PerformanceTest;

import com.lisasoft.awdip.AWDIPTestSupport;
import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * Test with growing bounding box and a fixed value of maxFeatures. The
 * number of the possibly returned feature must be higher than the maxFeatures
 * value, so the maxFeaters really limit the feature set.
 * 
 * The box with 640000 square kilometres returns 266 Features. Running tests
 * with maxFeatures 50, 100, 150, 200 and 250.
 * 
 * Rational: Does limiting the maximum features limit the performance hit, no
 * matter how big the bounding box is?
 * 
 * @author vmische
 *
 */
public class SiteLocationPerfTestBoundingBoxAndMaximumFeatures
        extends PerformanceTest  {
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
    

    public SiteLocationPerfTestBoundingBoxAndMaximumFeatures() {
        super(AWDIPTestSupport.getAwdipContext(), 1);

    }
    
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH) + "/wfs";
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

    
    /** Creates the body for a request with a bounding box filter
     * 
     * @param bbox the bounding box that should be used
     * @return
     */
    private String createBoundingBoxRequest(double[] bbox, int maxFeatures) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\"");
        request.append("maxFeatures=\""+maxFeatures+"\">");
        request.append(" <wfs:Query typeName=\"aw:siteLocation\">");
        request.append("<ogc:Filter>");
        request.append(Gml.createBoundingBoxFilter(bbox));
        request.append("</ogc:Filter></wfs:Query></wfs:GetFeature>");
        return request.toString();        
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
    

    public void testSiteLocationBoundingBox640000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "640000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "810000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox100000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1000000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1440000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1960000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "2560000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "3240000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000maxFeatures50()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox, 50);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "4000000");        

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }



    public void testSiteLocationBoundingBox640000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "6400000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "8100000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox100000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1440000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1960000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "2560000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "3240000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000maxFeatures100()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox, 100);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "4000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }




    public void testSiteLocationBoundingBox640000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "6400000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "8100000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1000000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1440000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1960000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "2560000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "3240000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000maxFeatures150()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox, 150);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "4000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }


    public void testSiteLocationBoundingBox640000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "6400000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "810000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1000000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1440000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1960000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "2560000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "3240000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000maxFeatures200()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox, 200);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "4000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }



    public void testSiteLocationBoundingBox640000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "6400000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox810000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "8100000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1000000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1000000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1440000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1440000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox1960000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "1960000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    } 

    public void testSiteLocationBoundingBox2560000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "2560000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox3240000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);
        properties.put("boundingBoxSize", "3240000");

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationBoundingBox4000000maxFeatures250()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox, 250);

        properties.put("boundingBox", bbox);

        data.put("body", body);
        properties.put(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        properties.put(TestExecutor.KEY_RESPONSE, response);            
    }

}

