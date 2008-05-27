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

public abstract class AbstractTestLocationMaximumFeatures
extends AbstractAwdipTest {
    
    // properties that should make it into the output
    static final String KEY_MAX_FEATURES = "params.maxFeatures";
    static final String KEY_BBOX = "params.boundingBox";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX,        
            KEY_MAX_FEATURES
    };
    
    /** Number of steps the from initial to maximum maximum Features 
     * (it's also the number of tests in this class)
     */
    static final int NUMBER_OF_STEPS = 15;
    
    /** Initial number of maximum features */
    int maxFeaturesInit;
    /** Step size to get from the initial to the maximum features */
    int maxFeaturesStep;
    
    /** Bounding box for this test */
    double[] bbox;
    
    /** Constructor for testing with a single thread a single call 
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationMaximumFeatures()
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        parseMaximumFeaturesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }

    /** Constructor for testing with a single thread but multiple calls
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationMaximumFeatures(int times)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times);
        parseMaximumFeaturesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    /** Constructor for testing with a single thread but multiple calls
     * distributed over time
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationMaximumFeatures(int times, double time,
            Random random)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, time, random);
        parseMaximumFeaturesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }    

    /** Constructor for testing with multiple threads
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestLocationMaximumFeatures(int times, int numThreads,
            int rampUp)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, numThreads, rampUp);

        parseMaximumFeaturesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    
    }
    
    /**
     * Parse configuration file for the maximum features test
     * Format:
     * 1st line: names of the fields
     * 2nd line:
     *     1st field: maximum features initial value 
     *     2nd field: maximum features maximal value
     *     3nd field: bounding box for the request (4 values comma separated in
     *         quotes ("min_lon,min_lat,max_lon,max_lat"))
     * 
     * @param file file to parse
     * @throws IOException 
     * @throws InvalidConfigFileException 
     */
    private void parseMaximumFeaturesConfiguration(String filename)
    throws IOException, InvalidConfigFileException {
        CSVReader csv = new CSVReader(new File(filename));  
                
        List<String[]> lines = csv.getLines();
        
        if (lines==null || lines.size()<2)
                throw new InvalidConfigFileException("2 lines expected!");

        
        String[] line = lines.get(1);
        this.maxFeaturesInit = new Integer(line[0]);
        int maxFeaturesMax =  new Integer(line[1]);
        String[] bbox = line[2].split(",");
        
        this.maxFeaturesStep = (maxFeaturesMax - this.maxFeaturesInit)
                / (NUMBER_OF_STEPS-1);
        
        if (bbox.length<4) {
            throw new InvalidConfigFileException(
                    "Fields with bounding boxes must contain 4 comma" +
                    "seperated values!");
        }
        
        this.bbox = new double[]{
                new Double(bbox[0]), new Double(bbox[1]), 
                new Double(bbox[2]), new Double(bbox[3])};
    }    
    
    
    /** Gets the maximum features on a certain step
     * @param step step to grow to
     * @return grown box
     */
    private int getCurrentMaxFeatures(int step) {
        return this.maxFeaturesInit + (maxFeaturesStep * step);
    }
    
    
    protected void prepareRequest(int step) {
        int maxFeatures = getCurrentMaxFeatures(step); 
        String body = Gml.createAndFilterMaxFeaturesRequest(
                getFeatureTypeName(),
                maxFeatures,
                Gml.createBoundingBoxFilter(bbox));
        
        data.put("body", body);

        putCallProperty(KEY_MAX_FEATURES, maxFeatures);
        putCallProperty(KEY_BBOX, bbox);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the maximum features test class. This is a test" +
                " a maximum of " + maxFeatures + " features and the bounding" +
		" box [" +bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]+"].");
        putCallProperty(TestExecutor.KEY_REQUEST, body);
    }
    
    
    protected void doRequest() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }
    
    protected void checkResponse()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathEvaluatesTo("true",
                "/wfs:FeatureCollection/@numberOfFeatures <= "
                +(Integer)getCallProperty(KEY_MAX_FEATURES),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
    }
    
    

    public void initLocationMaximumFeatures0(TestProperties props) {
        prepareRequest(0);
    }
    public void testLocationMaximumFeatures0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initLocationMaximumFeatures1(TestProperties props) {
        prepareRequest(1);
    }
    public void testLocationMaximumFeatures1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures2(TestProperties props) {
        prepareRequest(2);
    }
    public void testLocationMaximumFeatures2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures3(TestProperties props) {
        prepareRequest(3);
    }
    public void testLocationMaximumFeatures3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures4(TestProperties props) {
        prepareRequest(4);
    }
    public void testLocationMaximumFeatures4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures5(TestProperties props) {
        prepareRequest(5);
    }
    public void testLocationMaximumFeatures5()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures5()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures6(TestProperties props) {
        prepareRequest(6);
    }
    public void testLocationMaximumFeatures6()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures6()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures7(TestProperties props) {
        prepareRequest(7);
    }
    public void testLocationMaximumFeatures7()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures7()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures8(TestProperties props) {
        prepareRequest(8);
    }
    public void testLocationMaximumFeatures8()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures8()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures9(TestProperties props) {
        prepareRequest(9);
    }
    public void testLocationMaximumFeatures9()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures9()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures10(TestProperties props) {
        prepareRequest(10);
    }
    public void testLocationMaximumFeatures10()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures10()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures11(TestProperties props) {
        prepareRequest(11);
    }
    public void testLocationMaximumFeatures11()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures11()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures12(TestProperties props) {
        prepareRequest(12);
    }
    public void testLocationMaximumFeatures12()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures12()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures13(TestProperties props) {
        prepareRequest(13);
    }
    public void testLocationMaximumFeatures13()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures13()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures14(TestProperties props) {
        prepareRequest(14);
    }
    public void testLocationMaximumFeatures14()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures14()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    

    public void initLocationMaximumFeatures15(TestProperties props) {
        prepareRequest(15);
    }
    public void testLocationMaximumFeatures15()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkLocationMaximumFeatures15()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    
}
