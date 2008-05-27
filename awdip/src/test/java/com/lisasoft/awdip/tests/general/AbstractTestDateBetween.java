package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

public abstract class AbstractTestDateBetween extends AbstractAwdipTest {

    // properties that should make it into the output
    static final String KEY_SITE_NAME = "params.siteName";
    static final String KEY_PHENOMS_NAME = "params.phenomsName";
    static final String KEY_DATE_RANGE = "params.dateRange";
    static final String DATE_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_SITE_NAME,
            KEY_PHENOMS_NAME,
            KEY_DATE_RANGE 
    };
    
    /** Storing information about the site */
    List<String> sites = new ArrayList<String>();
    /** Storing information about the phenomenon */
    List<String> phenomenons = new ArrayList<String>();
    /** Storing information about the date range */
    List<long[]> dateRange = new ArrayList<long[]>();
    /** Step size of the date ranges the test will be performed on */
    List<Long> dateRangeStep = new ArrayList<Long>();
    
    /** The difference between two date ranges we test. The range will
     * grow equally from a very small range to the given maximum one.
     * (it's also the number of tests in this class)
     */
    static final int NUMBER_OF_STEPS = 5;
    
    

    
    /** Constructor for testing with a single thread a single call 
     * @throws InvalidConfigFileException 
     * @throws IOException 
     * @throws ParseException */
    public AbstractTestDateBetween()
    throws IOException, InvalidConfigFileException, ParseException {
        super(getAwdipContext(forcePropertyOutput));
        parseBetweenDatesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }

    /** Constructor for testing with a single thread but multiple calls
     * @throws InvalidConfigFileException 
     * @throws IOException 
     * @throws ParseException */
    public AbstractTestDateBetween(int times)
    throws IOException, InvalidConfigFileException, ParseException {
        super(getAwdipContext(forcePropertyOutput), times);
        parseBetweenDatesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    /** Constructor for testing with a single thread but multiple calls
     * distributed over time
     * @throws InvalidConfigFileException 
     * @throws IOException 
     * @throws ParseException */
    public AbstractTestDateBetween(int times, double time,
            Random random)
    throws IOException, InvalidConfigFileException, ParseException {
        super(getAwdipContext(forcePropertyOutput), times, time, random);
        parseBetweenDatesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }    

    /** Constructor for testing with multiple threads
     * @throws InvalidConfigFileException 
     * @throws IOException 
     * @throws ParseException */
    public AbstractTestDateBetween(int times, int numThreads,
            int rampUp)
    throws IOException, InvalidConfigFileException, ParseException {
        super(getAwdipContext(forcePropertyOutput), times, numThreads, rampUp);
        parseBetweenDatesConfiguration(
                (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + getConfigFilename());
    }
    
    
    /**
     * Parse configuration file for the single phenomenon between two dates for
     * one or more phenomena (5 different configurations)
     * Format:
     * 1st line: names of the fields
     * next 5 lines:
     *     1st field: name of a site
     *     2nd field: date range (comma separated, ISO: "yyyy-MM-dd,yyyy-MM-dd")
     *     3nd field: names of the phenomena (comma separated)
     * 
     * @param file file to parse
     * @throws IOException 
     * @throws InvalidConfigFileException 
     * @throws ParseException 
     */
    private void parseBetweenDatesConfiguration(String filename)
    throws IOException, InvalidConfigFileException, ParseException {
        CSVReader csv = new CSVReader(new File(filename));   
                
        List<String[]> lines = csv.getLines();
        
        if (lines==null || lines.size()<6)
                throw new InvalidConfigFileException("6 lines expected!");

        // remove header
        lines.remove(0);
        
        for (String[] line : lines) {
            this.sites.add(line[0]);
            String[] dateRangeString = line[1].split(",");
            this.phenomenons.add(line[2]);
            long[] dateRange = new long[]{
                    df.parse(dateRangeString[0]).getTime(),
                    df.parse(dateRangeString[1]).getTime()};
            this.dateRange.add(dateRange);
            // "*2" as we make the interval bigger from both sides
            this.dateRangeStep.add(
                    (dateRange[1] - dateRange[0])/((NUMBER_OF_STEPS-1)*2));
        }
    }    
    
    /**
     * Prepare the WFS request.
     *  
     *  @param sitePhenomNumber configuration file entry starting with 0
     *  @param step which step within the date range
     */
    protected void prepareRequest(int sitePhenomNumber, int step) {
        String site = this.sites.get(sitePhenomNumber); 
        String[] phenoms = this.phenomenons.get(sitePhenomNumber).split(",");
        long[] dateRange = new long[]{
                this.dateRange.get(sitePhenomNumber)[0]
                    +(((NUMBER_OF_STEPS-1)-step)
                            *this.dateRangeStep.get(sitePhenomNumber)),
                this.dateRange.get(sitePhenomNumber)[1]
                    -(((NUMBER_OF_STEPS-1)-step)
                            *this.dateRangeStep.get(sitePhenomNumber))};
        
        /** date Range formatted as strings */
        String[] dateRangeString = new String[]{
                df.format(new Date(dateRange[0])),
                df.format(new Date(dateRange[1]))};
        
        // The three filters are:
        // 1. site name property filter
        // 2. date range filter
        // 3. phenomena filters which consist of <ogc:Or> concatenated property
        //    filters 
        String[] filters = new String[3];
        filters[0] = Gml.createPropertyFilter("gml:name", site);
        filters[1] = Gml.createBetweenFilter(DATE_FIELD,
                dateRangeString[0], dateRangeString[1]);
        
        //String[] phenomFilters = new String[phenoms.length];
        StringBuffer phenomFilters = new StringBuffer();
        phenomFilters.append("<ogc:Or>");
        for (int i=0; i<phenoms.length; i++)
            phenomFilters.append(Gml.createPropertyFilter("aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name",
                    phenoms[i]));
        phenomFilters.append("</ogc:Or>");        
        filters[2] = phenomFilters.toString();

        String body = Gml.createAndFilterRequest(getFeatureTypeName(), filters);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        putCallProperty(KEY_SITE_NAME, site);
        putCallProperty(KEY_PHENOMS_NAME, phenoms);
        putCallProperty(KEY_DATE_RANGE, dateRangeString);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the testing a single phenomenon between two dates" +
                " test class.");
    }
    
    
    /**
     * Creates an XPath query for counting the appearance of dates on, before
     * or after a certain date. It selects date values (yyyy-MM-dd or format)
     * with XPath and compare it to an asserted date.
     * It will perform: datesReturnedFromXPath operator date
     * Write the operator as e.g. ">" and *not* "&gt;" 
     * 
     * @param date Date the values selected by the XPath query should be
     *        compared to (in format: yyyy-MM-dd or yyyMMdd)
     * @param operator Operator to compare the dates
     * @return XPath expression to get the count of these dates
     */
    private String createCountDatesXpath(String operator, String date) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteSinglePhenomTimeSeries/aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry[translate(substring(.,1,10),'-','') " +
                operator + " translate('" +
                date + "','-','') ])";
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
        
        // make sure that there were no features outside of the date range
        // returned
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        "<",
                        ((String[])getCallProperty(KEY_DATE_RANGE))[0]),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        ">",
                        ((String[])getCallProperty(KEY_DATE_RANGE))[1]),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
    }
    
    
    
    
    
    public void initBetweenDates0(TestProperties props) {
        prepareRequest(0, 0);
    }
    public void testBetweenDates0()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates0()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates1(TestProperties props) {
        prepareRequest(0, 1);
    }
    public void testBetweenDates1()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates1()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates2(TestProperties props) {
        prepareRequest(0, 2);
    }
    public void testBetweenDates2()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates2()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates3(TestProperties props) {
        prepareRequest(0, 3);
    }
    public void testBetweenDates3()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates3()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates4(TestProperties props) {
        prepareRequest(0, 4);
    }
    public void testBetweenDates4()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates4()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    

    public void initBetweenDates5(TestProperties props) {
        prepareRequest(1, 0);
    }
    public void testBetweenDates5()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates5()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates6(TestProperties props) {
        prepareRequest(1, 1);
    }
    public void testBetweenDates6()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates6()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates7(TestProperties props) {
        prepareRequest(1, 2);
    }
    public void testBetweenDates7()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates7()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates8(TestProperties props) {
        prepareRequest(1, 3);
    }
    public void testBetweenDates8()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates8()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates9(TestProperties props) {
        prepareRequest(1, 4);
    }
    public void testBetweenDates9()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates9()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    
    public void initBetweenDates10(TestProperties props) {
        prepareRequest(2, 0);
    }
    public void testBetweenDates10()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates10()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates11(TestProperties props) {
        prepareRequest(2, 1);
    }
    public void testBetweenDates11()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates11()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates12(TestProperties props) {
        prepareRequest(2, 2);
    }
    public void testBetweenDates12()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates12()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates13(TestProperties props) {
        prepareRequest(2, 3);
    }
    public void testBetweenDates13()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates13()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates14(TestProperties props) {
        prepareRequest(2, 4);
    }
    public void testBetweenDates14()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates14()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    
    public void initBetweenDates15(TestProperties props) {
        prepareRequest(3, 0);
    }
    public void testBetweenDates15()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates15()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates16(TestProperties props) {
        prepareRequest(3, 1);
    }
    public void testBetweenDates16()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates16()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates17(TestProperties props) {
        prepareRequest(3, 2);
    }
    public void testBetweenDates17()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates17()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates18(TestProperties props) {
        prepareRequest(3, 3);
    }
    public void testBetweenDates18()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates18()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates19(TestProperties props) {
        prepareRequest(3, 4);
    }
    public void testBetweenDates19()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates19()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    
    public void initBetweenDates20(TestProperties props) {
        prepareRequest(4, 0);
    }
    public void testBetweenDates20()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates20()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates21(TestProperties props) {
        prepareRequest(4, 1);
    }
    public void testBetweenDates21()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates21()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates22(TestProperties props) {
        prepareRequest(4, 2);
    }
    public void testBetweenDates22()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates22()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates23(TestProperties props) {
        prepareRequest(4, 3);
    }
    public void testBetweenDates23()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates23()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
    
    public void initBetweenDates24(TestProperties props) {
        prepareRequest(4, 4);
    }
    public void testBetweenDates24()
    throws HttpException, IOException {
        doRequest();
    }
    public void checkBetweenDates24()
    throws XpathException, SAXException, IOException{
        checkResponse();
    }
}





