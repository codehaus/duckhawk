package com.lisasoft.awdip;

import static com.lisasoft.awdip.AWDIPTestSupport.KEY_GS_PATH;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_HOST;
import static com.lisasoft.awdip.AWDIPTestSupport.KEY_PORT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.PerformanceTestRunner;
import org.duckhawk.core.StressTestRunner;
import org.duckhawk.core.TestRunner;
import org.duckhawk.junit3.AbstractDuckHawkTest;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;


/**
 * General tests use this abstract class. The type (performance, conformance,
 * stress) will be set by the corresponding constructor.
 * 
 * @author vmische
 *
 */
public abstract class AbstractAwdipTest extends AbstractDuckHawkTest {
    protected static Communication comm;

    /** data sent to the server (body of the POST message) */
    protected HashMap<String, String> data = new HashMap<String, String>();
    
    /** Request sent to the server */
    protected Request request;
    
    // for stress tests 
    protected int times;
    private int numThreads;
    private int rampUp;

    // for performance tests
    private double time;
    private Random random;

    private enum TestType {
        PERFORMANCE,
        CONFORMANCE,
        STRESS
    }
    
    private TestType testType;

    /** Date format for XPath expressions */
    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
    
    /**
     * Constructor for conformance test
     * @param context
     */
    public AbstractAwdipTest(TestContext context) {
        super(context);
        testType = TestType.CONFORMANCE;
    }


    /**
     * Constructor for performance test
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times) {
        super(context);
        this.times = times;
        testType = TestType.PERFORMANCE;
    }
    
    /**
     * Constructor for performance test (with certain requests within a certain
     * time)
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times, double time,
            Random random) {
        super(context);        
        this.times = times;
        this.time = time;
        this.random = random;
    }
    
        
    
    /**
     * Constructor for stress test
     * @param context
     */
    public AbstractAwdipTest(TestContext context, int times,
            int numThreads, int rampUp) {
        super(context);
        this.times = times;
        this.numThreads = numThreads;
        this.rampUp = rampUp;
        testType = TestType.STRESS;
    }
    
    

    /**
     * Returns the feature type of the current test
     * @return the feature type of the current test
     */
    public abstract String getFeatureTypeName();

    
    /**
     * Returns the filename of the configuration file for the current test
     * @return the filename of the configuration file for the current test
     */
    public abstract String getConfigFilename();
    
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String host = (String) getEnvironment(KEY_HOST);
        int port = (Integer) getEnvironment(KEY_PORT);
        String path = (String) getEnvironment(KEY_GS_PATH);

        comm = new Communication(host, port);
        request = new Request(RequestMethod.POST, "/" + path);
    }

    
    protected String sanitizeForFilename(String input) {
        return input.replace(':', '_');
    }


    protected TestRunner getTestRunner(TestContext context) {
        TestRunner runner;
        
        switch (testType) {
        case CONFORMANCE:
            runner = new ConformanceTestRunner(context, buildTestExecutor());            
            break;
        case PERFORMANCE:
            if (random != null)
                runner = new PerformanceTestRunner(context, buildTestExecutor(),
                        times, time, random);
            else
                runner = new PerformanceTestRunner(context, buildTestExecutor(),
                        times);
            break;
        case STRESS:
        default:
            runner = new StressTestRunner(context, buildTestExecutor(), times,
                    numThreads, rampUp);
            break;
        }
        return runner;
    }
}
