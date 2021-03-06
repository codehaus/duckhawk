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
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.tests.conformance.AwdipConformanceTest;
import com.lisasoft.awdip.util.CSVReader;
import com.lisasoft.awdip.util.Gml;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteSinglePhenomDateAnyTest extends AbstractAwdipTest {
    /** the feature type to test */
    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries";
    final static String CONFIG_FILE = "/SiteSinglePhenomTimeSeriesTestDateAny.csv";

    /** XPath to the phenomenon name */
    static final String PHENOM_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name";


    // properties that should make it into the output
    static final String KEY_SITE_NAME = "params.siteName";
    static final String KEY_PHENOM_NAME = "params.phenomName";
    static final String KEY_NUM_MEASURES = "test.numMeasures";

    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[] {
            KEY_SITE_NAME,
            KEY_PHENOM_NAME,
            KEY_NUM_MEASURES};

    /** Name of the site */
    String site;
    /** Name of the  phenomena */
    String phenomenon;

    /**
     * Constructor for testing with a single thread a single call
     * 
     * @throws InvalidConfigFileException
     * @throws IOException
     */
    public SiteSinglePhenomDateAnyTest(String testNameSuffix, String site,
            String phenomenon) throws IOException,
            InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testAnyDate");
        setTestMethodSuffix(testNameSuffix);
        this.site = site;
        this.phenomenon = phenomenon;
    }
    
    
    /**
     * Parse configuration file for the single site and a single phenomenon test
     * Format:
     * 1st line: names of the fields
     * next lines:
     *     1st field: name of a site
     *     2nd field: names of the phenomenon
     */
    public static Test suite() throws InvalidConfigFileException, IOException {
        // read CSV file
        String filename = (String) getAwdipContext().getEnvironment().get(KEY_TESTS_CONFIG_DIR)
                + CONFIG_FILE;
        CSVReader csv = new CSVReader(new File(filename));
        List<String[]> lines = csv.getLines();

        if (lines==null)
            throw new InvalidConfigFileException(
                    "File doesn't contain any data!");
        
        // remove header
        lines.remove(0);
        
        String[] sites = new String[lines.size()];
        String[] phenomenons = new String[lines.size()];

        for (int i=0; i<lines.size(); i++) {
            sites[i] = lines.get(i)[0];
            phenomenons[i] = lines.get(i)[1];
        }

        Set<TestType> performTests = getPerformTests();
        TestSuite suite = new TestSuite();

        for(TestType testType : performTests) {
            for (int i=0; i<lines.size(); i++) {
                SiteSinglePhenomDateAnyTest test =
                        new SiteSinglePhenomDateAnyTest(
                                i+"", sites[i], phenomenons[i]);
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

        return suite;
    }
    
    
    /**
     * Counts the number of measures in a response
     * @param input XML response
     * @return
     */
    private static int countMeasures(String input)
    throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if (prefix.equals("wfs"))
                        return "http://www.opengis.net/wfs";
                    else if (prefix.equals("gml"))
                        return "http://www.opengis.net/gml";
                    else if (prefix.equals("aw"))
                        return "http://www.water.gov.au/awdip";
                    else if (prefix.equals("om"))
                        return "http://www.opengis.net/om/1.0";
                    else if (prefix.equals("cv"))
                        return "http://www.opengis.net/cv/0.2.1";
                    return null;
                }

                public String getPrefix(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }

                public Iterator<String> getPrefixes(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }
        });

        InputSource inputSource = new InputSource(new StringReader(input));
        return ((Double)xpath.evaluate(
                "count(wfs:FeatureCollection/gml:featureMembers/aw:SiteSinglePhenomTimeSeries/aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element)",
                inputSource, XPathConstants.NUMBER)).intValue();
    }
    
    

    public void initAnyDate(TestProperties context) {
        String body = Gml.createAndFilterRequest(FEATURE_TYPE_NAME,
                Gml.createPropertyFilter("gml:name", site),
                Gml.createPropertyFilter(PHENOM_FIELD, phenomenon));

        data.put("body", body);
        context.put(TestExecutor.KEY_REQUEST, body);

        context.put(KEY_SITE_NAME, site);
        context.put(KEY_PHENOM_NAME, phenomenon);
        context.put(TestExecutor.KEY_DESCRIPTION,
                "Part of the testing a single phenomenon at any date test" +
                "class.");
    }

    public void testAnyDate() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }

    public void checkAnyDate() throws XpathException, SAXException, IOException,
        XPathExpressionException {
        String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE); 
        XMLAssert.assertXpathExists("/wfs:FeatureCollection/gml:featureMembers",
                response);
        
        // save the number of returned measures in the call properties
        putCallProperty(KEY_NUM_MEASURES, countMeasures(response));
        
        if (getTestType()==TestType.conformance) {
            AwdipConformanceTest.NumberOfFeaturesCheck(response);
            AwdipConformanceTest.validate(response);
        }
    }
}
