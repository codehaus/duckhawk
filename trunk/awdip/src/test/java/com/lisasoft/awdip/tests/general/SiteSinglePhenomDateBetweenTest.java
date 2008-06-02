package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_TESTS_CONFIG_DIR;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadNumThreads;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadRampUp;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadTimes;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteSinglePhenomDateBetweenTest extends AbstractAwdipTest {
    final static String CONFIG_FILE = "/SiteSinglePhenomTimeSeriesTestDateBetween.csv";
    
    /** XPath to date property */
    static final String DATE_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry";    
    /** XPath to phenomenon property */
    static final String PHENOM_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name";

    // IDEA vmische perhaps make it configurable 
    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries";

    
    // properties that should make it into the output
    static final String KEY_SITE_NAME = "params.siteName";
    static final String KEY_PHENOMS_NAME = "params.phenomsName";
    static final String KEY_DATE_RANGE = "params.dateRange";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_SITE_NAME,
            KEY_PHENOMS_NAME,
            KEY_DATE_RANGE 
    };
    
    /** Name of the site */
    String site;
    /** Name of one or more  phenomena */
    String[] phenomena;
    /** Storing information about the date range */
    long[] dateRange = new long[2];


    /**
     * Constructor for testing with a single thread a single call
     * 
     * @throws InvalidConfigFileException
     * @throws IOException
     */
    public SiteSinglePhenomDateBetweenTest(String testNameSuffix, String site,
            String[] phenomena, long[] dateRange)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testBetweenDate");
        setTestMethodSuffix(testNameSuffix);
        this.site = site;
        this.phenomena = phenomena;
        this.dateRange = dateRange;
    }
    
    /**
     * Parse configuration file for the single site between two dates for
     * one or more phenomena
     * Format:
     * 1st line: names of the fields
     * next lines:
     *     1st field: number of steps till full range
     *     2nd field: name of a site
     *     3rd field: date range (comma separated, ISO: "yyyy-MM-dd,yyyy-MM-dd")
     *     4th field: names of the phenomena (comma separated)     
     */
    public static Test suite() throws Exception {
        // Parsing configuration file
        //------------------------------
        
        // read CSV file
        String filename = (String)getAwdipContext().getEnvironment()
                .get(KEY_TESTS_CONFIG_DIR) + CONFIG_FILE;
        CSVReader csv = new CSVReader(new File(filename));   

        // store the values in lists, so you need to parse the file only once
        List<String> sites = new ArrayList<String>();
        List<String[]> phenomenons = new ArrayList<String[]>();
        List<long[]> dateRanges = new ArrayList<long[]>();
        /** number of steps that should be performed to until the full given
         * date range is reached */
        List<Integer> numberOfSteps = new ArrayList<Integer>();
        
        List<String[]> lines = csv.getLines();
        
        if (lines==null)
            throw new InvalidConfigFileException(
                    "File doesn't contain any data!");
        
        // remove header
        lines.remove(0);

        for (String[] line : lines) {
            sites.add(line[1]);
            String[] dateRangeString = line[2].split(",");
            phenomenons.add(line[3].split(","));
            dateRanges.add(new long[]{
                    df.parse(dateRangeString[0]).getTime(),
                    df.parse(dateRangeString[1]).getTime()});
            numberOfSteps.add(new Integer(line[0]));
        }

        // creating tests
        //------------------------------
        

        TestSuite suite = new TestSuite();
/*            
        // create conformance tests
        for (int i=0; i<sites.size(); i++) {
            // we want a growing range
            long[][] growingDateRange = divideDateRange(dateRanges.get(i),
                   numberOfSteps.get(i));
            for (long[] range : growingDateRange) {
                DateBetweenParametrizedTest test =
                    new DateBetweenParametrizedTest(
                            i+"_"+dff.format(new Date(range[1])), sites.get(i),
                            phenomenons.get(i), range);
                suite.addTest(test);                
            }
        }
*/    
        // create performance tests
        for (int i=0; i<sites.size(); i++) {
            // we want a growing range
            long[][] growingDateRange = divideDateRange(dateRanges.get(i),
                   numberOfSteps.get(i));
            for (long[] range : growingDateRange) {
                SiteSinglePhenomDateBetweenTest test =
                    new SiteSinglePhenomDateBetweenTest(
                            i+"_"+dff.format(new Date(range[1])), sites.get(i),
                            phenomenons.get(i), range);
                test.configureAsPerformanceTest(getPerfTimes());
                suite.addTest(test);
            }
        }
            
/*
        // create load tests
        for (int i=0; i<sites.size(); i++) {
            // we want a growing range
            long[][] growingDateRange = divideDateRange(dateRanges.get(i),
                   numberOfSteps.get(i));
            for (long[] range : growingDateRange) {
                DateBetweenParametrizedTest test =
                    new DateBetweenParametrizedTest(
                            i+"_"+dff.format(new Date(range[1])), sites.get(i),
                            phenomenons.get(i), range);
                test.configureAsLoadTest(getLoadTimes(), getLoadNumThreads(),
                        getLoadRampUp());
                suite.addTest(test);
            }            
        }
*/
        return suite;
    }
    
    /**
     * Divide a date range in equidistant ranges. Starting with a small one,
     * ending with the given range. Each bigger range wraps all smaller ranges
     * (like a matryoshka doll)
     */
    private static long[][] divideDateRange(long[] dateRange,
            int numberOfPieces) {
        long[][] dateRanges = new long[numberOfPieces][];
        long pieceSize = (dateRange[1] - dateRange[0])/numberOfPieces;
        
        for (int i=0; i<numberOfPieces; i++) { 
            dateRanges[i] = new long[]{
                    (dateRange[0] + pieceSize*(numberOfPieces-1-i)),
                    (dateRange[1] - pieceSize*(numberOfPieces-1-i))
            };
        }

        return dateRanges;
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
    
    
    
    
    /**
     * Prepare the WFS request.
     *  
     *  @param sitePhenomNumber configuration file entry starting with 0
     *  @param step which step within the date range
     */
    public void initBetweenDate(TestProperties context) {
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
        //for (int i=0; i<phenomena.length; i++)
        for (String phenom : phenomena) 
            phenomFilters.append(
                    Gml.createPropertyFilter(PHENOM_FIELD,phenom));
        phenomFilters.append("</ogc:Or>");        
        filters[2] = phenomFilters.toString();

        String body = Gml.createAndFilterRequest(FEATURE_TYPE_NAME, filters);

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        context.put(KEY_SITE_NAME, site);
        context.put(KEY_PHENOMS_NAME, phenomena);
        context.put(KEY_DATE_RANGE, dateRangeString);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the testing a single phenomenon between two dates" +
                " test class.");
    }
    
    

    
    public void testBetweenDate() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }
    
    public void checkBetweenDate()
    throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathExists(
                "/wfs:FeatureCollection/gml:featureMembers",
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
        
        // make sure that there were no features outside of the date range
        // returned
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        "<",
                        df.format(new Date(dateRange[0]))),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));        
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpath(
                        ">",
                        df.format(new Date(dateRange[1]))),
                (String)getCallProperty(TestExecutor.KEY_RESPONSE));
    }
    
}
