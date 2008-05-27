package com.lisasoft.awdip.tests.reliability;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_DESCRIPTION;
import static com.lisasoft.awdip.AWDIPTestSupport.getAwdipContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestProperties;
import org.duckhawk.junit3.PerformanceTest;
import org.xml.sax.SAXException;





/**
 * Pick a random reliability test on each call and runs it
 * 
 * @author vmische
 *
 */
public class ReliabilityAggregator extends PerformanceTest {
    SiteLocationTest siteLocationTest;
    SiteSinglePhenomTimeSeriesTest siteSinglePhenomTimeSeries;
    
    /** Number of reliability test that can be randomly chosen from */ 
    final static int NUMBER_OF_TESTS = 4;
    
    Random random = new Random();
    
    /** Random number of the current call */ 
    int currentRandom;
    int nextRandom;
    
    /** make properties accessible for whole class */
    TestProperties aggregatorProps;
    
    
    // properties used in the tests
    static final String KEY_BBOX = "params.boundingBox";;
    static final String KEY_DATE_START = "params.dateStart";
    static final String KEY_DATE_END = "params.dateEnd";
    
    /** force properties to be in the output, even if "null" */
    static final String[] forcePropertyOutput = new String[]{
            KEY_BBOX,
            KEY_DATE_START,
            KEY_DATE_END            
    };     
    
    public ReliabilityAggregator() {
        super(getAwdipContext(forcePropertyOutput), 5);
        random.setSeed(100);
        currentRandom = random.nextInt(NUMBER_OF_TESTS);
    }
    
    public void initReliabilty(TestProperties props) {
        props.put(KEY_DESCRIPTION,
                "Aggregator for reliabilty tests. These include:\n" +
                " - varying bounding box\n" +
                " - changing date intervals");  
        this.aggregatorProps = props;
    }
    public void testReliabilty()
    throws HttpException, IOException, ParseException, InterruptedException,
    XpathException, SAXException  {
        TestProperties callProperties = (TestProperties)getCallPropertyObject();
        callProperties.clear();
        
        switch (currentRandom) {
        case 0:
            siteLocationTest = new SiteLocationTest(null);
            siteLocationTest.testVariableBoundingBox();
            siteLocationTest.fillCallProperties(callProperties);
            siteLocationTest.checkVariableBoundingBox();
            break;
        case 1:
            siteSinglePhenomTimeSeries = new SiteSinglePhenomTimeSeriesTest(null);
            siteSinglePhenomTimeSeries.testOnePhenomenonTypePREC_ToDBetweenTwoDates();
            siteSinglePhenomTimeSeries.fillCallProperties(callProperties);
            siteSinglePhenomTimeSeries.checkOnePhenomenonTypePREC_ToDBetweenTwoDates();
            break;
        case 2:
            siteSinglePhenomTimeSeries = new SiteSinglePhenomTimeSeriesTest(null);
            siteSinglePhenomTimeSeries.testOnePhenomenonTypePREC_ToMBetweenTwoDates();
            siteSinglePhenomTimeSeries.fillCallProperties(callProperties);
            siteSinglePhenomTimeSeries.checkOnePhenomenonTypePREC_ToMBetweenTwoDates();
            break;
        case 3:
            siteSinglePhenomTimeSeries = new SiteSinglePhenomTimeSeriesTest(null);
            siteSinglePhenomTimeSeries.testOnePhenomenonTypePREC_ToYBetweenTwoDates();
            siteSinglePhenomTimeSeries.fillCallProperties(callProperties);
            siteSinglePhenomTimeSeries.checkOnePhenomenonTypePREC_ToYBetweenTwoDates();
            break;
        }
        callProperties.put("test."+KEY_DESCRIPTION,
                "Aggregator for reliabilty tests. These include:\n" +
                " - varying bounding box\n" +
                " - changing date intervals");  
        currentRandom = random.nextInt(NUMBER_OF_TESTS);
    }
}
