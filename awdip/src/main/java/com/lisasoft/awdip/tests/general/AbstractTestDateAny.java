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

public abstract class AbstractTestDateAny extends AbstractAwdipTest {

    // properties that should make it into the output
    static final String KEY_SITE_NAME = "params.siteName";
    static final String KEY_PHENOM_NAME = "params.phenomName";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_SITE_NAME,
            KEY_PHENOM_NAME
    }; 
    
    /** Storing information about the site */
    List<String> sites = new ArrayList<String>();
    /** Storing information about the phenomenon */
    List<String> phenomenons = new ArrayList<String>();

    /** Constructor for testing with a single thread a single call 
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestDateAny()
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        parseAnyDateConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }

    /** Constructor for testing with a single thread but multiple calls
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestDateAny(int times)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times);
        parseAnyDateConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    /** Constructor for testing with a single thread but multiple calls
     * distributed over time
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestDateAny(int times, double time,
            Random random)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, time, random);
        parseAnyDateConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }    

    /** Constructor for testing with multiple threads
     * @throws InvalidConfigFileException 
     * @throws IOException */
    public AbstractTestDateAny(int times, int numThreads,
            int rampUp)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput), times, numThreads, rampUp);
        parseAnyDateConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    

    /**
     * Parse configuration file for the single phenomenon at any date for ten
     * site/phenomenon types
     * Format:
     * 1st line: names of the fields
     * next 5 lines:
     *     1st field: name of a site
     *     2nd field: name of the phenomenon
     * 
     * @param file file to parse
     * @throws IOException 
     * @throws InvalidConfigFileException 
     */
    private void parseAnyDateConfiguration(String filename)
    throws IOException, InvalidConfigFileException {
        CSVReader csv = new CSVReader(new File(filename));   
                
        List<String[]> lines = csv.getLines();
        
        if (lines==null || lines.size()<6)
                throw new InvalidConfigFileException("6 lines expected!");

        // remove header
        lines.remove(0);
        
        for (String[] line : lines) {
            this.sites.add(line[0]);
            this.phenomenons.add(line[1]);
        }
    }
    
    protected void prepareRequest(int sitePhenomNumber) {
        String site = this.sites.get(sitePhenomNumber); 
        String phenomenon = this.phenomenons.get(sitePhenomNumber);

        String body = Gml.createAndFilterRequest(getFeatureTypeName(),
                Gml.createPropertyFilter("gml:name", site),
                Gml.createPropertyFilter("aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name", phenomenon));
 
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        putCallProperty(KEY_SITE_NAME, site);
        putCallProperty(KEY_PHENOM_NAME, phenomenon);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the testing a single phenomenon at any date test" +
                "class.");
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
    }

    
    
    public void initAnyDate0(TestProperties props) {
        prepareRequest(0);
    }
    public void testAnyDate0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkAnyDate0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initAnyDate1(TestProperties props) {
        prepareRequest(1);
    }
    public void testAnyDate1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkAnyDate1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initAnyDate2(TestProperties props) {
        prepareRequest(2);
    }
    public void testAnyDate2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkAnyDate2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initAnyDate3(TestProperties props) {
        prepareRequest(3);
    }
    public void testAnyDate3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkAnyDate3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initAnyDate4(TestProperties props) {
        prepareRequest(4);
    }
    public void testAnyDate4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkAnyDate4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }    
}
