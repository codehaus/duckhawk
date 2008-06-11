package com.lisasoft.awdip.tests.general;

import static com.lisasoft.awdip.AWDIPTestSupport.*;

import java.io.File;
import java.io.IOException;
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

public class SiteLocationMaximumFeaturesTest extends AbstractAwdipTest {
    /** the feature type to test */
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation";
    final static String CONFIG_FILE = "/SiteLocationTestMaximumFeatures.csv";

    // properties that should make it into the output
    static final String KEY_BBOX = "params.boundingBox";
    static final String KEY_MAX_FEATURES = "params.maxFeatures";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX,
            KEY_MAX_FEATURES
    };

    /** bounding box that is used in the current test */
    double[] bbox;
    
    /** maximum features that should be returned */
    int maxFeatures;
    
    
    /**
     * Constructor for testing with a single thread a single call
     * 
     * @throws InvalidConfigFileException
     * @throws IOException
     */
    public SiteLocationMaximumFeaturesTest(String testNameSuffix,
            double[] bbox, int maxFeatures)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testMaximumFeatures");
        setTestMethodSuffix(testNameSuffix);
        this.bbox = bbox;
        this.maxFeatures = maxFeatures;
    }
    
    
    /**
     * Parse configuration file for the maximum features test
     * Format:
     * 1st line: names of the fields
     * next lines:
     *     1st field: number of steps 
     *     2nd field: maximum features initial value 
     *     3rd field: maximum features maximal value
     *     4th field: bounding box for the request (4 values comma separated in
     *         quotes ("min_lon,min_lat,max_lon,max_lat"))
     */
    public static Test suite() throws Exception {
        // read CSV file
        String filename = (String) getAwdipContext().getEnvironment().get(KEY_TESTS_CONFIG_DIR)
                + CONFIG_FILE;
        CSVReader csv = new CSVReader(new File(filename));      
        
        List<String[]> lines = csv.getLines();
        
        if (lines==null)
                throw new InvalidConfigFileException(
                        "File doesn't contain any data!");

        double[][] bbox = new double[lines.size()][4];
        int[] steps = new int[lines.size()];
        int[] maxFeaturesInit = new int[lines.size()];
        int[] maxFeaturesStep = new int[lines.size()];
        
        // remove header
        lines.remove(0);
       
        for (int i=0; i<lines.size(); i++) {
            String[] bboxString = lines.get(i)[3].split(",");

            if (bboxString.length<4) {
                throw new InvalidConfigFileException(
                        "Fields with bounding boxes must contain 4 comma" +
                        "seperated values!");
            }
            
            bbox[i] = new double[]{
                    new Double(bboxString[0]),
                    new Double(bboxString[1]), 
                    new Double(bboxString[2]),
                    new Double(bboxString[3])};

            steps[i] = new Integer(lines.get(i)[0]);
            maxFeaturesInit[i] = new Integer(lines.get(i)[1]);
            int maxFeaturesMax = new Integer(lines.get(i)[2]);
            
            // only one step => use maximum features
            if (steps[i]>1)
                maxFeaturesStep[i] =
                    (maxFeaturesMax-maxFeaturesInit[i]) / (steps[i]-1);
            else
                maxFeaturesStep[i] = maxFeaturesMax;
        }
        

        Set<TestType> performTests = getPerformTests();
        TestSuite suite = new TestSuite();

        for(TestType testType : performTests) {
            for (int i=0; i<maxFeaturesInit.length; i++) {
                for (int j=0; j<steps[i]; j++) {
                    int maxFeatures =
                            maxFeaturesInit[i] + maxFeaturesStep[i]*j;
                    SiteLocationMaximumFeaturesTest test =
                        new SiteLocationMaximumFeaturesTest(
                                i+""+j, bbox[i], maxFeatures);
            
                    switch(testType) {
                    case performance:
                        test.configureAsPerformanceTest(getPerfTimes());
                        break;
                    case stress:
                        test.configureAsLoadTest(getLoadTimes(),
                                getLoadNumThreads(), getLoadRampUp());
                        break;
                    case conformance:
                        // nothing needs to be done, as the constructor
                        // initializes it as conformance
                        break;
                    }
                    suite.addTest(test);
                }
            }
        }

        return suite;
    }
    
    public void initMaximumFeatures(TestProperties context) {
        String body = Gml.createAndFilterMaxFeaturesRequest(
                FEATURE_TYPE_NAME,
                maxFeatures,
                Gml.createBoundingBoxFilter(bbox));
        
        data.put("body", body);
        context.put(TestExecutor.KEY_REQUEST, body);

        context.put(KEY_BBOX, bbox);
        context.put(KEY_MAX_FEATURES, maxFeatures);
        context.put(TestExecutor.KEY_DESCRIPTION,
                "Part of the maximum features test class. This is a test" +
                " a maximum of " + maxFeatures + " features and the bounding" +
                " box [" +bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]+"].");
    }

    public void testMaximumFeatures() throws HttpException, IOException {
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);
    }

    public void checkMaximumFeatures()
    throws XpathException, SAXException, IOException {
        String response = (String) getCallProperty(TestExecutor.KEY_RESPONSE); 
        XMLAssert.assertXpathExists("/wfs:FeatureCollection/gml:featureMembers",
                response);
        
        // make sure that there were no features outside of the bounding box
        XMLAssert.assertXpathEvaluatesTo("0",
                Gml.createCountNotWithinBoundingBoxXpath(bbox), response);
        
        // make sure there were not more features returned than maxFeatures was
        // set to
        XMLAssert.assertXpathEvaluatesTo("true",
                "/wfs:FeatureCollection/@numberOfFeatures <= " + maxFeatures,
                response); 
        
        if (getTestType()==TestType.conformance) {
            AwdipConformanceTest.NumberOfFeaturesCheck(response);
            AwdipConformanceTest.validate(response);
        }
    }
}
