/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestType;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.tests.conformance.AwdipConformanceTest;
import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteSamplingStatisticsBoundingBoxTest extends AbstractAwdipTest {
    /** the feature type to test */
    final static String FEATURE_TYPE_NAME = "aw:SiteSamplingStatistics";
    final static String CONFIG_FILE = "/SiteSamplingStatisticsTestBoundingBox.csv";

    /** XPath to aw:availableFrom property */
    static final String DATE_FIELD_FROM = "aw:sampledPhenomenon/aw:SummaryStatistics/aw:availableFrom";    
    /** XPath to aw:availableTo property */
    static final String DATE_FIELD_TO = "aw:sampledPhenomenon/aw:SummaryStatistics/aw:availableTo";    
    /** XPath to phenomenon property */
    static final String PHENOM_FIELD = "aw:sampledPhenomenon/aw:SummaryStatistics/aw:definition/aw:phenomenonDef/aw:name";
    
    // properties that should make it into the output
    static final String KEY_BBOX = "params.boundingBox";
    static final String KEY_PHENOMS_NAME = "params.phenomsName";
    static final String KEY_DATE_RANGE = "params.dateRange";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
        KEY_BBOX,
        KEY_PHENOMS_NAME,
        KEY_DATE_RANGE
    };

    /** bounding box that is used in the current test */
    double[] bbox;
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
    public SiteSamplingStatisticsBoundingBoxTest(String testNameSuffix,
            double[] bbox, long[] dateRange, String[] phenomena)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testBoundingBox");
        setTestMethodSuffix(testNameSuffix);
        this.bbox = bbox;
        this.dateRange = dateRange;
        this.phenomena = phenomena;
    }
    
    
    /**
     * Parse configuration file for growing bounding box test
     * Format:
     * 1st line: names of the fields
     * next lines:
     *     1st field: number of steps that should be used to grow the bounding
     *         box from initial to the maximum extent
     *     2nd field: initial bounding box, 4 values comma separated in quotes
     *         ("min_lon,min_lat,max_lon,max_lat")
     *     3rd field: maximal extend bounding box 4 values comma separated in
     *         quotes ("min_lon,min_lat,max_lon,max_lat")
     *     4th field: date range (comma separated, ISO: "yyyy-MM-dd,yyyy-MM-dd")
     *     5th field: names of the phenomena (comma separated)
     */
    public static Test suite()
    throws InvalidConfigFileException, IOException, ParseException {
        // read CSV file
        String filename = (String) getAwdipContext().getEnvironment().get(KEY_TESTS_CONFIG_DIR)
                + CONFIG_FILE;
        CSVReader csv = new CSVReader(new File(filename));      
        
        List<String[]> lines = csv.getLines();
        
        if (lines==null)
                throw new InvalidConfigFileException(
                        "File doesn't contain any data!");

        double[][] bboxInit = new double[lines.size()][4];
        double[][] bboxStep = new double[lines.size()][4];
        int[] steps = new int[lines.size()];
        long[][] dateRanges = new long[lines.size()][2];
        String[][] phenomenons = new String[lines.size()][];
        
        // remove header
        lines.remove(0);
       
        for (int i=0; i<lines.size(); i++) {
            String[] init = lines.get(i)[1].split(",");
            String[] max = lines.get(i)[2].split(",");

            if (init.length<4 || max.length<4) {
                throw new InvalidConfigFileException(
                        "Fields with bounding boxes must contain 4 comma" +
                        "seperated values!");
            }
            
            String[] dateRangeString = lines.get(i)[3].split(",");
            if (dateRangeString.length<2) {
                throw new InvalidConfigFileException(
                        "Fields with date ranges must contain 2 comma" +
                        "seperated values!");
            }        

            steps[i] = new Integer(lines.get(i)[0]);
            
            // only one step => use maximum bounding box
            if (steps[i]>1) {
                bboxInit[i] = new double[]{
                        new Double(init[0]),
                        new Double(init[1]), 
                        new Double(init[2]),
                        new Double(init[3])};

                bboxStep[i] = new double[]{
                        (new Double(max[0])-bboxInit[i][0])/(steps[i]-1),
                        (new Double(max[1])-bboxInit[i][1])/(steps[i]-1), 
                        (new Double(max[2])-bboxInit[i][2])/(steps[i]-1),
                        (new Double(max[3])-bboxInit[i][3])/(steps[i]-1)};
            }
            else
                bboxStep[i] = new double[]{
                    new Double(max[0]), new Double(max[1]), 
                    new Double(max[2]), new Double(max[3])};
            
    
            dateRanges[i] = new long[]{
                    df.parse(dateRangeString[0]).getTime(),
                    df.parse(dateRangeString[1]).getTime()};            

            phenomenons[i] = (lines.get(i)[4].split(","));
        }
        

        Set<TestType> performTests = getPerformTests();
        TestSuite suite = new TestSuite();

        for(TestType testType : performTests) {
            for (int i=0; i<bboxInit.length; i++) {
                for (int j=0; j<steps[i]; j++) {
                    double[] bbox =  new double[]{
                            bboxInit[i][0]+(bboxStep[i][0]*j),
                            bboxInit[i][1]+(bboxStep[i][1]*j), 
                            bboxInit[i][2]+(bboxStep[i][2]*j),
                            bboxInit[i][3]+(bboxStep[i][3]*j),
                    };
                    SiteSamplingStatisticsBoundingBoxTest test =
                        new SiteSamplingStatisticsBoundingBoxTest(i+""+j,
                                bbox, dateRanges[i], phenomenons[i]);
            
                    switch(testType) {
                    case performance:
                        test.configureAsPerformanceTest(getPerfTimes());
                        break;
                    case stress:
                        test.configureAsLoadTest(getLoadTimes(),
                                getLoadNumThreads(), getLoadRampUp());
                        break;
                    case conformance:
                        // nothing needs to be done, as the constructor initializes
                        // it as conformance
                        break;
                    }
                    suite.addTest(test);
                }
            }
        }
        return suite;
    }
    
    
    /**
     * Creates an XPath query for counting the appearance of dates on, before
     * or after a certain date. It selects date values (yyyy-MM-dd or format)
     * with XPath and compare it to an asserted date.
     * It will perform: datesReturnedFromXPath operator date
     * Write the operator as e.g. ">" and *not* "&gt;" 
     * This one is for aw:availableFrom.
     * 
     * @param date Date the values selected by the XPath query should be
     *        compared to (in format: yyyy-MM-dd or yyyMMdd)
     * @param operator Operator to compare the dates
     * @return XPath expression to get the count of these dates
     */
    private String createCountDatesXpathFrom(String operator, String date) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteSamplingStatistics/" +
                DATE_FIELD_FROM + "[translate(substring(.,1,10),'-','') " +
                operator + " translate('" +
                date + "','-','') ])";
    }
    
    /**
     * Creates an XPath query for counting the appearance of dates on, before
     * or after a certain date. It selects date values (yyyy-MM-dd or format)
     * with XPath and compare it to an asserted date.
     * It will perform: datesReturnedFromXPath operator date
     * Write the operator as e.g. ">" and *not* "&gt;" 
     * This one is for aw:availableTo.
     * 
     * @param date Date the values selected by the XPath query should be
     *        compared to (in format: yyyy-MM-dd or yyyMMdd)
     * @param operator Operator to compare the dates
     * @return XPath expression to get the count of these dates
     */    
    private String createCountDatesXpathTo(String operator, String date) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteSamplingStatistics/" + 
                DATE_FIELD_TO + "[translate(substring(.,1,10),'-','') " +
                operator + " translate('" +
                date + "','-','') ])";
    }
    
    
    
    public void initBoundingBox(TestProperties context) {
        /** date Range formatted as strings */
        String[] dateRangeString = new String[]{
                df.format(new Date(dateRange[0])),
                df.format(new Date(dateRange[1]))};
        
        // The four filters are:
        // 1. bounding box
        // 2. date range filter for the start of the measures 
        // 3. date range filter for the end of the measures 
        // 4. phenomena filters which consist of <ogc:Or> concatenated property
        //    filters 
        String[] filters = new String[4];
        filters[0] = Gml.createBoundingBoxFilter(bbox);
        filters[1] = Gml.createLessOrEqualFilter(DATE_FIELD_FROM,
                dateRangeString[1]);

        filters[2] = Gml.createGreaterOrEqualFilter(DATE_FIELD_TO,
                dateRangeString[0]);

        StringBuffer phenomFilters = new StringBuffer();
        phenomFilters.append("<ogc:Or>");
        for (String phenom : phenomena) 
            phenomFilters.append(
                    Gml.createPropertyFilter(PHENOM_FIELD,phenom));
        phenomFilters.append("</ogc:Or>");        
        filters[3] = phenomFilters.toString();

        String body = Gml.createAndFilterRequest(FEATURE_TYPE_NAME, filters);
        
        data.put("body", body);
        context.put(TestExecutor.KEY_REQUEST, body);
        
        context.put(KEY_BBOX, bbox);
        context.put(KEY_PHENOMS_NAME, phenomena);
        context.put(KEY_DATE_RANGE, dateRangeString);
        context.put(TestExecutor.KEY_DESCRIPTION,
                "Part of the growing bounding box test class (with sites" +
                "between a certain date and certain (one or more) phenomena).");
    }

    public void testBoundingBox() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }

    public void checkBoundingBox()
    throws XpathException, SAXException, IOException {
        String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE); 
        XMLAssert.assertXpathExists("/wfs:FeatureCollection/gml:featureMembers",
                response);
        
        // make sure that there were no features outside of the bounding box
        XMLAssert.assertXpathEvaluatesTo("0",
                Gml.createCountNotWithinBoundingBoxXpath(bbox), response);
        
        // make sure that there were no features outside of the date range
        // returned
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpathFrom(
                        ">",
                        df.format(new Date(dateRange[1]))),
                response);        
        XMLAssert.assertXpathEvaluatesTo("0",
                createCountDatesXpathTo(
                        "<",
                        df.format(new Date(dateRange[0]))),
                response);
        
        if (getTestType()==TestType.conformance) {
            AwdipConformanceTest.NumberOfFeaturesCheck(response);
            AwdipConformanceTest.validate(response);
        }
    }
}
