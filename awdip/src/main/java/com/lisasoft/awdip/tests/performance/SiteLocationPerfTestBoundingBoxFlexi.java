package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.junit3.PerformanceTest;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;

public class SiteLocationPerfTestBoundingBoxFlexi extends PerformanceTest {
    static Communication comm;

    /** data sent to the server (path and body of the POST message) */
    HashMap<String, String> data = new HashMap<String, String>();

    /** Request sent to the server */
    Request request;
    
    String response = "";

    /** properties that should make it into the output */
    static final String KEY_BBOX = "params.boundingBox";
    static final String KEY_BBOX_SIZE = "params.boundingBoxSize";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX,
            KEY_BBOX_SIZE
    }; 

    
    /** Bounding box to start with, and size changes for the tests **/
    double[] bboxInit;
    double[] bboxMax;
    double[] bboxDif;

    /**  size changes of the bounding box for the tests
     *  key = ID for test
     *  1. value grow along latitude (in both directions)
     *  2. value grow along longitude (in both directions)
     */
    Map<String,double[]> bboxGrow;
    

    public SiteLocationPerfTestBoundingBoxFlexi() throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), 5);
        
        CSVReader csv = new CSVReader("src/main/resources/tests/performance/SiteLocationPerfTestBoundingBoxFlexi.csv");
                
        List<List<String>> lines = csv.getLines();
        
        if(lines == null || lines.size() < 2) {
        	throw new InvalidConfigFileException("2 lines expected!");
        }
        
        List<String> init = lines.get(0);
        List<String> max = lines.get(1);
        
        if(max.size() < 4 || init.size() < 4) {
        	throw new InvalidConfigFileException("For lines 4 values are expected!");
        }
        
        this.bboxInit = new double[]{new Double(init.get(0)), new Double(init.get(1)), 
        		new Double(init.get(2)), new Double(init.get(3))};
        this.bboxMax = new double[]{new Double(max.get(0)), new Double(max.get(1)), 
        		new Double(max.get(2)), new Double(max.get(3))};
    }
    
    
    @Override
    protected void setUp() throws Exception {
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH);
        
        comm = new Communication(host, port);
        this.request = new Request(RequestMethod.POST, "/" + path);
        
        this.bboxDif = new double[]{(bboxMax[0]-bboxInit[0]), (bboxMax[1]-bboxInit[1]), 
        					   (bboxMax[2]-bboxInit[2]), (bboxMax[3]-bboxInit[3])};

        /** size changes of the bounding box for the tests
         * key = ID for test
         *  1. value grow along latitude (in both directions)
         *  2. value grow along longitude (in both directions)
         */
        bboxGrow = new HashMap<String, double[]>();
        bboxGrow.put("bb10000",  new double[]{0.05, 0.05});
        bboxGrow.put("bb40000",  new double[]{0.1, 0.1});
        bboxGrow.put("bb90000",  new double[]{0.15, 0.15});
        bboxGrow.put("bb160000",  new double[]{0.2, 0.2});
        bboxGrow.put("bb250000", new double[]{0.25, 0.25});
        bboxGrow.put("bb360000", new double[]{0.3, 0.3});
        bboxGrow.put("bb490000", new double[]{0.35, 0.35});
        bboxGrow.put("bb640000", new double[]{0.4, 0.4});
        bboxGrow.put("bb810000", new double[]{0.45, 0.45});
        bboxGrow.put("bb1000000", new double[]{0.5, 0.5});
        bboxGrow.put("bb1440000", new double[]{0.6, 0.6});
        bboxGrow.put("bb1960000", new double[]{0.7, 0.7});
        bboxGrow.put("bb2560000", new double[]{0.8, 0.8});
        bboxGrow.put("bb3240000", new double[]{0.9, 0.9});
        bboxGrow.put("bb4000000", new double[]{1, 1});
    }

    
    /** Creates the body for a request with a bounding box filter
     * 
     * @param bbox the bounding box that should be used
     * @return
     */
    private String createBoundingBoxRequest(double[] bbox) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">");
        request.append(" <wfs:Query typeName=\"aw:SiteLocation\">");
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
        
        System.out.println("off: "+offsets[0]);
        
        double[] bbox = new double[]{
                (bboxInit[0]+(this.bboxDif[0]*offsets[0])), (bboxInit[1]+(this.bboxDif[1]*offsets[1])),   
                (bboxInit[2]+(this.bboxDif[2]*offsets[0])), (bboxInit[3]+(this.bboxDif[3]*offsets[1])),   
        };
        
        System.out.println("CR0 "+bbox[0]);
        System.out.println("CR1 "+bbox[1]);
        System.out.println("CR2 "+bbox[2]);
        System.out.println("CR3 "+bbox[3]);
        
        return bbox;
    }
    

    public void testSiteLocationBoundingBox100()
    throws HttpException, IOException {
        String body = createBoundingBoxRequest(bboxInit);

        putCallProperty("KEY_BBOX", bboxInit);
        putCallProperty("params.boundingBoxSize", "100");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void checkSiteLocationBoundingBox100()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("1",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox10000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb10000");      
        
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "10000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void checkSiteLocationBoundingBox10000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("10",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox40000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb40000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "40000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }    
    
    public void checkSiteLocationBoundingBox40000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("37",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox90000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb90000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "90000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox90000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("70",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }

    
    public void testSiteLocationBoundingBox160000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb160000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "160000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void checkSiteLocationBoundingBox160000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("110",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    

    public void testSiteLocationBoundingBox250000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb250000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "250000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox250000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("160",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    

    public void testSiteLocationBoundingBox360000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb360000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "360000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox360000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("181",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox490000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb490000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "490000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox490000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("217",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox640000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb640000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "640000");        

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox640000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("266",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox810000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb810000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "810000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox810000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("324",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox1000000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1000000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "1000000");        

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox1000000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("444",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox1440000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1440000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "1440000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox1440000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("709",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox1960000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb1960000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "1960000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    } 
    
    public void checkSiteLocationBoundingBox1960000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("871",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox2560000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb2560000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "2560000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void checkSiteLocationBoundingBox2560000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("1020",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox3240000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb3240000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "3240000");        

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }
    
    public void checkSiteLocationBoundingBox3240000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("1153",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }


    public void testSiteLocationBoundingBox4000000()
    throws HttpException, IOException {
        double[] bbox = getGrownBbox("bb4000000");
        String body = createBoundingBoxRequest(bbox);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(KEY_BBOX_SIZE, "4000000");

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);            
    }

    public void checkSiteLocationBoundingBox4000000()
    throws XpathException, SAXException, IOException{
        XMLAssert.assertXpathEvaluatesTo("1327",
                "//wfs:FeatureCollection/@numberOfFeatures",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
  
}