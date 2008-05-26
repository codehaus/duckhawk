package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public abstract class AbstractTestLocationBoundingBoxAndMaximumFeatures
extends AbstractAwdipTest {
    
    // properties that should make it into the output
    static final String KEY_BBOX = "params.boundingBox";
    static final String KEY_MAX_FEATURES = "params.maxFeatures";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX,
            KEY_MAX_FEATURES
    };

    /** Bounding box to start with **/
    List<double[]> bboxesInit = new ArrayList<double[]>();
    /** Size the bounding box grows per test method. It scales linearly from
     * bboxInit to bboxMax
     */
    List<double[]> bboxesStep = new ArrayList<double[]>();

    /** Number of steps the bounding box should grow to full extent bboxMax */
    static final int NUMBER_OF_STEPS = 5;

    /** Maximum number of features */
    List<Integer> maxFeaturesList = new ArrayList<Integer>();
    
    /** Constructor for testing with a single thread a single call 
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBoxAndMaximumFeatures()
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }

    /** Constructor for testing with a single thread but multiple calls
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBoxAndMaximumFeatures(int times)
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
    public AbstractTestLocationBoundingBoxAndMaximumFeatures(int times,
            double time, Random random)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, time, random);
        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }    

    /** Constructor for testing with multiple threads
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationBoundingBoxAndMaximumFeatures(int times,
            int numThreads, int rampUp)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, numThreads, rampUp);

        parseBoundingBoxConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    
    }
    
   
   
    /**
     * Parse configuration file for growing bounding box test with maximum
     * features
     * Format:
     * 1st line: names of the fields
     * next 5 lines:
     *     1st field: number of maximum features
     *     2nd field: initial bounding box, 4 values comma separated in quotes
     *         ("min_lon,min_lat,max_lon,max_lat")
     *     3rd field: maximal extend bounding box 4 values comma separated in
     *         quotes ("min_lon,min_lat,max_lon,max_lat")
     */
    private void parseBoundingBoxConfiguration(String filename)
    throws IOException, InvalidConfigFileException {
        CSVReader csv = new CSVReader(new File(filename));  ;        
                
        List<String[]> lines = csv.getLines();
        
        if (lines==null || lines.size()<6)
                throw new InvalidConfigFileException("6 lines expected!");
        
        // remove header
        lines.remove(0);

        for (String[] line : lines) {
            String maxFeatures = line[0];
            String[] bboxInitStrings = line[1].split(",");
            String[] bboxMax = line[2].split(",");

            this.maxFeaturesList.add(new Integer(maxFeatures));
            
            if (bboxMax.length<4 || bboxInitStrings.length<4) {
                throw new InvalidConfigFileException(
                        "Fields with bounding boxes must contain 4 comma" +
                        "seperated values!");
            }

            double[] bboxInit = new double[]{
                    new Double(bboxInitStrings[0]),
                    new Double(bboxInitStrings[1]), 
                    new Double(bboxInitStrings[2]),
                    new Double(bboxInitStrings[3])};
            this.bboxesInit.add(bboxInit);
            
            this.bboxesStep.add(new double[]{
                    (new Double(bboxMax[0])-bboxInit[0])/(NUMBER_OF_STEPS-1),
                    (new Double(bboxMax[1])-bboxInit[1])/(NUMBER_OF_STEPS-1), 
                    (new Double(bboxMax[2])-bboxInit[2])/(NUMBER_OF_STEPS-1),
                    (new Double(bboxMax[3])-bboxInit[3])/(NUMBER_OF_STEPS-1)});        
        }
    }
    
    /** Grows the initial bounding box to a certain step
     * @param step step to grow to
     * @return grown box
     */
    private double[] getGrownBbox(int box, int step) {
        return new double[]{
                bboxesInit.get(box)[0]-(bboxesStep.get(box)[0]*step),
                bboxesInit.get(box)[1]-(bboxesStep.get(box)[1]*step), 
                bboxesInit.get(box)[0]+(bboxesStep.get(box)[0]*step),
                bboxesInit.get(box)[1]+(bboxesStep.get(box)[1]*step)
        };
    }
    
    
    
    protected void prepareRequest(int index, int step) {
        double[] bbox = getGrownBbox(index, step); 
        String body = Gml.createAndFilterMaxFeaturesRequest(
                getFeatureTypeName(),
                maxFeaturesList.get(index),
                Gml.createBoundingBoxFilter(bbox));

        data.put("body", body);

        putCallProperty(KEY_MAX_FEATURES, maxFeaturesList.get(index));
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
        
        // make sure there number of returned features below the maxFeatures
        XMLAssert.assertXpathEvaluatesTo("true",
                "/wfs:FeatureCollection/@numberOfFeatures <= "
                +(Integer)getCallProperty(KEY_MAX_FEATURES),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    

    public void initSiteLocationBoundingBoxMaxFeaturesFirst0(TestProperties props) {
        prepareRequest(0, 0);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFirst0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFirst0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFirst1(TestProperties props) {
        prepareRequest(0, 1);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFirst1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFirst1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBoxMaxFeaturesFirst2(TestProperties props) {
        prepareRequest(0, 2);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFirst2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFirst2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFirst3(TestProperties props) {
        prepareRequest(0, 3);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFirst3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFirst3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFirst4(TestProperties props) {
        prepareRequest(0, 4);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFirst4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFirst4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    
    
    
    public void initSiteLocationBoundingBoxMaxFeaturesSecond0(TestProperties props) {
        prepareRequest(1, 0);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesSecond0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesSecond0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesSecond1(TestProperties props) {
        prepareRequest(1, 1);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesSecond1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesSecond1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBoxMaxFeaturesSecond2(TestProperties props) {
        prepareRequest(1, 2);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesSecond2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesSecond2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesSecond3(TestProperties props) {
        prepareRequest(1, 3);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesSecond3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesSecond3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesSecond4(TestProperties props) {
        prepareRequest(1, 4);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesSecond4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesSecond4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    
    
    
    
    
    public void initSiteLocationBoundingBoxMaxFeaturesThird0(TestProperties props) {
        prepareRequest(2, 0);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesThird0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesThird0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesThird1(TestProperties props) {
        prepareRequest(2, 1);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesThird1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesThird1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBoxMaxFeaturesThird2(TestProperties props) {
        prepareRequest(2, 2);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesThird2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesThird2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesThird3(TestProperties props) {
        prepareRequest(2, 3);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesThird3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesThird3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesThird4(TestProperties props) {
        prepareRequest(2, 4);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesThird4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesThird4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }  
    

    
    public void initSiteLocationBoundingBoxMaxFeaturesFourth0(TestProperties props) {
        prepareRequest(3, 0);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFourth0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFourth0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFourth1(TestProperties props) {
        prepareRequest(3, 1);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFourth1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFourth1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBoxMaxFeaturesFourth2(TestProperties props) {
        prepareRequest(3, 2);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFourth2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFourth2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFourth3(TestProperties props) {
        prepareRequest(3, 3);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFourth3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFourth3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFourth4(TestProperties props) {
        prepareRequest(3, 4);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFourth4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFourth4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }  
  
    
    

    public void initSiteLocationBoundingBoxMaxFeaturesFifth0(TestProperties props) {
        prepareRequest(4, 0);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFifth0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFifth0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFifth1(TestProperties props) {
        prepareRequest(4, 1);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFifth1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFifth1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }

    public void initSiteLocationBoundingBoxMaxFeaturesFifth2(TestProperties props) {
        prepareRequest(4, 2);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFifth2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFifth2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFifth3(TestProperties props) {
        prepareRequest(4, 3);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFifth3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFifth3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initSiteLocationBoundingBoxMaxFeaturesFifth4(TestProperties props) {
        prepareRequest(4, 4);
    }
    public void testSiteLocationBoundingBoxMaxFeaturesFifth4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkSiteLocationBoundingBoxMaxFeaturesFifth4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }  
}
