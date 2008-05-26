package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public abstract class AbstractTestLocationBoundingBox
extends AbstractAwdipTest {
   
    // properties that should make it into the output
    static final String KEY_BBOX = "params.boundingBox";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX
    }; 

    /** Number of steps the bounding box should grow to full extent bboxMax
     * (it's also the number of tests in this class)
     */
    static final int NUMBER_OF_STEPS = 15;
    
    
    /** Bounding box to start with **/
    double[] bboxInit;
    /** Size the bounding box grows per test method. It scales linearly from
     * bboxInit to bboxMax
     */
    double[] bboxStep;


    /** Constructor for testing with a single thread a single call 
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBox()
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }

    /** Constructor for testing with a single thread but multiple calls
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBox(int times)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times);
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    /** Constructor for testing with a single thread but multiple calls
     * distributed over time
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBox(int times, double time,
            Random random)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, time, random);
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }    

    /** Constructor for testing with multiple threads
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBox(int times, int numThreads,
            int rampUp)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, numThreads, rampUp);
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    

    
    

    /**
     * Parse configuration file for growing bounding box test
     * Format:
     * 1st line: names of the fields
     * 2nd line:
     *     1st field: initial bounding box, 4 values comma separated in quotes
     *         ("min_lon,min_lat,max_lon,max_lat")
     *     2nd field: maximal extend bounding box 4 values comma separated in
     *         quotes ("min_lon,min_lat,max_lon,max_lat")
     * 
     * @param file file to parse
     * @throws IOException 
     * @throws InvalidConfigFileException 
     */
    private void parseBoundingBoxConfiguration(String filename)
    throws IOException, InvalidConfigFileException {
        CSVReader csv = new CSVReader(new File(filename));      
                
        List<String[]> lines = csv.getLines();
        
        if (lines==null || lines.size()<2)
                throw new InvalidConfigFileException("2 lines expected!");

        
        String[] bboxes = lines.get(1);
        String[] init = bboxes[0].split(",");
        String[] max = bboxes[1].split(",");
        
        if (init.length<4 || max.length<4) {
            throw new InvalidConfigFileException(
                    "Fields with bounding boxes must contain 4 comma" +
                    "seperated values!");
        }
        
        this.bboxInit = new double[]{
                new Double(init[0]), new Double(init[1]), 
                new Double(init[2]), new Double(init[3])};
        
        this.bboxStep = new double[]{
                (new Double(max[0])-bboxInit[0])/(NUMBER_OF_STEPS-1),
                (new Double(max[1])-bboxInit[1])/(NUMBER_OF_STEPS-1), 
                (new Double(max[2])-bboxInit[2])/(NUMBER_OF_STEPS-1),
                (new Double(max[3])-bboxInit[3])/(NUMBER_OF_STEPS-1)};
    }
    
   
    /** Grows the initial bounding box to a certain step
     * @param step step to grow to
     * @return grown box
     */
    private double[] getGrownBbox(int step) {
        return new double[]{
                bboxInit[0]-(bboxStep[0]*step), bboxInit[1]-(bboxStep[1]*step), 
                bboxInit[0]+(bboxStep[0]*step), bboxInit[1]+(bboxStep[1]*step),
        };
    }
    
    
    
    protected void prepareRequest(int step) {
        double[] bbox = getGrownBbox(step); 
        String body = Gml.createAndFilterRequest(getFeatureTypeName(),
                Gml.createBoundingBoxFilter(bbox));

        data.put("body", body);

        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the growing bounding box test class. This is a test" +
                "with the bounding box ["+
                bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]+"].");
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    
    protected void doRequest() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }
    
    protected void checkResponse()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathExists(
                "/wfs:FeatureCollection/gml:featureMembers",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
        
        // make sure that there were no features outside of the bounding box
        XMLAssert.assertXpathEvaluatesTo("0",
                Gml.createCountNotWithinBoundingBoxXpath(
                        (double[])getCallProperty(KEY_BBOX)),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
    }
    

    
    
    
    public void initSiteLocationBoundingBox0(TestProperties props) {
        prepareRequest(0);
    }
    public void testSiteLocationBoundingBox0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox1(TestProperties props) {
        prepareRequest(1);
    }
    public void testSiteLocationBoundingBox1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox2(TestProperties props) {
        prepareRequest(2);
    }
    public void testSiteLocationBoundingBox2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox3(TestProperties props) {
        prepareRequest(3);
    }
    public void testSiteLocationBoundingBox3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox4(TestProperties props) {
        prepareRequest(4);
    }
    public void testSiteLocationBoundingBox4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox5(TestProperties props) {
        prepareRequest(5);
    }
    public void testSiteLocationBoundingBox5()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox5()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox6(TestProperties props) {
        prepareRequest(6);
    }
    public void testSiteLocationBoundingBox6()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox6()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox7(TestProperties props) {
        prepareRequest(7);
    }
    public void testSiteLocationBoundingBox7()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox7()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox8(TestProperties props) {
        prepareRequest(8);
    }
    public void testSiteLocationBoundingBox8()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox8()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox9(TestProperties props) {
        prepareRequest(9);
    }
    public void testSiteLocationBoundingBox9()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox9()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox10(TestProperties props) {
        prepareRequest(10);
    }
    public void testSiteLocationBoundingBox10()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox10()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox11(TestProperties props) {
        prepareRequest(11);
    }
    public void testSiteLocationBoundingBox11()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox11()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox12(TestProperties props) {
        prepareRequest(12);
    }
    public void testSiteLocationBoundingBox12()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox12()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox13(TestProperties props) {
        prepareRequest(13);
    }
    public void testSiteLocationBoundingBox13()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox13()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox14(TestProperties props) {
        prepareRequest(14);
    }
    public void testSiteLocationBoundingBox14()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox14()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBox15(TestProperties props) {
        prepareRequest(15);
    }
    public void testSiteLocationBoundingBox15()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBox15()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
}
