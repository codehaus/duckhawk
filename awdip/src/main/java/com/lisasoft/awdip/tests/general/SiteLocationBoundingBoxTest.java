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

public class SiteLocationBoundingBoxTest extends AbstractAwdipTest {
    /** the feature type to test */
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation";
    final static String CONFIG_FILE = "/SiteLocationTestBoundingBox.csv";

    // properties that should make it into the output
    static final String KEY_BBOX = "params.boundingBox";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX
    };

    /** bounding box that is used in the current test */
    double[] bbox;
    
    
    
    /**
     * Constructor for testing with a single thread a single call
     * 
     * @throws InvalidConfigFileException
     * @throws IOException
     */
    public SiteLocationBoundingBoxTest(String testNameSuffix, double[] bbox)
    throws IOException, InvalidConfigFileException {
        super(getAwdipContext(forcePropertyOutput));
        setName("testBoundingBox");
        setTestMethodSuffix(testNameSuffix);
        this.bbox = bbox;
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

        double[][] bboxInit = new double[lines.size()][4];
        double[][] bboxStep = new double[lines.size()][4];
        int[] steps = new int[lines.size()];
        
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
                    SiteLocationBoundingBoxTest test =
                        new SiteLocationBoundingBoxTest(i+""+j, bbox);
            
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
    
    public void initBoundingBox(TestProperties context) {
        String body = Gml.createAndFilterRequest(FEATURE_TYPE_NAME,
                Gml.createBoundingBoxFilter(bbox));

        data.put("body", body);
        context.put(TestExecutor.KEY_REQUEST, body);

        context.put(KEY_BBOX, bbox);
        context.put(TestExecutor.KEY_DESCRIPTION,
                "Part of the growing bounding box test class. This is a test" +
                "with the bounding box ["+
                bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]+"].");
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
        
        if (getTestType()==TestType.conformance) {
            AwdipConformanceTest.NumberOfFeaturesCheck(response);
            AwdipConformanceTest.validate(response);
        }
    }
}
