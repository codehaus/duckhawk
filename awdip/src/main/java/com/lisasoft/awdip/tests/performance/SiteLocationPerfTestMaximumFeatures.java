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
 * Test with bounding box around the whole data, increasing maxmimumFeatures.
 * The current dataset has approx. 20000 database entries, not all of them
 * will make it into features
 * 
 * Rational: How many features can be requested at once without problems
 * 
 * @author vmische
 *
 */
public class SiteLocationPerfTestMaximumFeatures extends PerformanceTest  {
    static Communication comm;
    
    /** data sent to the server (path and body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();

    /** Request sent to the server */
    Request request;
    
    String response = "";

    
    /** Minimum Bounding Box that covers all the data */
    double[] bboxAll;

    /**  size changes of the bounding box for the tests
     *  key = ID for test
     *  1. value grow along latitude (in both directions)
     *  2. value grow along longitude (in both directions)
     */
    HashMap<String,double[]> bboxGrow;
    

    public SiteLocationPerfTestMaximumFeatures() {
        super(AWDIPTestSupport.getAwdipContext(), 5);

    }
    
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getTestProperty(KEY_HOST);
        int port = (Integer) getTestProperty(KEY_PORT);
        String path = (String) getTestProperty(KEY_GS_PATH);
        comm = new Communication(host, port);

        request = new Request(RequestMethod.POST,
                "/" + path + "/TestWfsPost");

        data.put("url", "http://" + host + ":" + port + "/"
                + path + "/wfs");
        
        bboxAll = new double[]{52.0, -81.0, 1-149.0, 8.0};
    }

    public String createMaxFeaturesRequest(String typeName, int maxFeatures) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\"");
        request.append("maxFeatures=\""+maxFeatures+"\">");
        request.append(" <wfs:Query typeName=\""+typeName+"\">");
        request.append("<ogc:Filter>");
        request.append(Gml.createBoundingBoxFilter(bboxAll));
        request.append("</ogc:Filter></wfs:Query></wfs:GetFeature>");
        return request.toString();
    }


    public void testSiteLocationMaxFeatures1()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 1);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures10()
    throws HttpException, IOException {
        String body = createMaxFeaturesRequest("aw:SiteLocation", 10);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures100()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 100);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures200()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 200);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures300()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 300);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures400()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 400);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures500()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 500);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures600()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 600);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void testSiteLocationMaxFeatures700()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 700);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures800()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 800);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures900()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 900);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures1000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 1000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures1500()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 1500);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures2000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 2000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures2500()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 2500);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures3000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 3000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures3500()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 3500);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures4000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 4000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures4500()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 4500);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures5000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 5000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures6000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 6000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures7000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 7000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures8000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 8000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures9000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 9000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures10000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 10000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures11000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 11000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    


    public void testSiteLocationMaxFeatures12000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 12000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures13000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 13000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures14000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 14000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures15000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 15000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures16000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 16000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures17000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 17000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures18000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 18000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures19000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 19000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    

    public void testSiteLocationMaxFeatures20000()
    throws HttpException, IOException {
        String body =  createMaxFeaturesRequest("aw:SiteLocation", 20000);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);         
    }    
}

