package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

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

public class DateAnyParametrizedTest extends AbstractAwdipTest {

    static final String DATE_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:observedProperty/swe:Phenomenon/gml:name";

    // properties that should make it into the output
    static final String KEY_SITE_NAME = "params.siteName";

    static final String KEY_PHENOM_NAME = "params.phenomName";

    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries";

    final static String CONFIG_FILE = "/performance/SiteSinglePhenomTimeSeriesTestDateAny.csv";

    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[] { KEY_SITE_NAME, KEY_PHENOM_NAME };

    String site;

    String phenomenon;

    /**
     * Constructor for testing with a single thread a single call
     * 
     * @throws InvalidConfigFileException
     * @throws IOException
     */
    public DateAnyParametrizedTest(String site, String phenomenon, String suffix) throws IOException,
            InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testAnyDate");
        setTestMethodSuffix(suffix);
        setTestClassSuffix("Conformance");
        this.site = site;
        this.phenomenon = phenomenon;
    }
    
    @Override
    public void configureAsLoadTest(int times, int numThreads, int rampUp) {
        super.configureAsLoadTest(times, numThreads, rampUp);
        setTestClassSuffix("Load");
    }
    
    @Override
    public void configureAsPerformanceTest(int times) {
        super.configureAsPerformanceTest(times);
        setTestClassSuffix("Performance");
    }
    
    public static Test suite() throws Exception {
        // read CSV file
        String filename = (String) getAwdipContext().getEnvironment().get(KEY_TESTS_CONFIG_DIR)
                + CONFIG_FILE;
        CSVReader csv = new CSVReader(new File(filename));
        List<String[]> lines = csv.getLines();
        if (lines == null || lines.size() < 6)
            throw new InvalidConfigFileException("6 lines expected!");
        
        // remove header
        lines.remove(0);
        
        // configure conformance tests
        TestSuite suite = new TestSuite();
        int i = 1;
        for (String[] line : lines) {
            suite.addTest(new DateAnyParametrizedTest(line[0], line[1], i + ""));
            i++;
        }
        
        // configure performance tests
        i = 1;
        for (String[] line : lines) {
            DateAnyParametrizedTest test = new DateAnyParametrizedTest(line[0], line[1], i + "");
            test.configureAsPerformanceTest(getPerfTimes());
            suite.addTest(test);
            i++;
        }

        // configure load tests
        i = 1;
        for (String[] line : lines) {
            DateAnyParametrizedTest test = new DateAnyParametrizedTest(line[0], line[1], i + "");
            test.configureAsLoadTest(getLoadTimes(), getLoadNumThreads(), getLoadRampUp());
            suite.addTest(test);
            i++;
        }
        
        return suite;
    }

    public void initAnyDate(TestProperties context) {
        String body = Gml.createAndFilterRequest(FEATURE_TYPE_NAME, Gml.createPropertyFilter(
                "gml:name", site), Gml.createPropertyFilter(DATE_FIELD, phenomenon));

        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);

        putCallProperty(KEY_SITE_NAME, site);
        putCallProperty(KEY_PHENOM_NAME, phenomenon);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Part of the testing a single phenomenon at any date test" + "class.");
    }

    public void testAnyDate() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }

    public void checkAnyDate() throws XpathException, SAXException, IOException {
        XMLAssert.assertXpathExists("/wfs:FeatureCollection/gml:featureMembers",
                (String) getCallProperty(TestExecutor.KEY_RESPONSE));
    }

}
