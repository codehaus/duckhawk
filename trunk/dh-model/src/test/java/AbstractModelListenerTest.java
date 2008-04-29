import junit.framework.TestCase;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.duckhawk.report.listener.AbstractModelListener;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;
import org.easymock.EasyMock;

/**
 * @author   Andrea Aime (TOPP)
 */
public class AbstractModelListenerTest extends TestCase {

    public TestCallDetail detail;

    public TestResult endResult;

    public TestResult startResult;

    private TestModelLister listener;

    private TestMetadata metadata;
    
    private TestMetadata metadata2;

    private TestPropertiesImpl emptyProperties;

    private TestExecutor executor;
    

    private class TestModelLister extends AbstractModelListener {

        @Override
        protected void handleDetail(TestCallDetail detail) throws Exception {
            AbstractModelListenerTest.this.detail = detail;

        }

        @Override
        protected void testEnded(TestResult result) throws Exception {
            AbstractModelListenerTest.this.endResult = result;

        }

        @Override
        protected void testStarting(TestResult result) throws Exception {
            AbstractModelListenerTest.this.startResult = result;
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        listener = new TestModelLister();
        metadata = new TestMetadata("ThisIsTheTest", "Product", "VapourWareEdition");
        metadata2 = new TestMetadata("ThisIsTheSecondTest", "Product", "VapourWareEdition");
        emptyProperties = new TestPropertiesImpl();
        executor = EasyMock.createNiceMock(TestExecutor.class);
    }
    
    public void testStart() {
        // invoke the listener
        TestProperties props = new TestPropertiesImpl();
        props.put("totalTime", new Double(2.3));
        listener.testRunStarting(metadata, props, 20);
        
        // make sure results are the one we're expecting
        assertNotNull(startResult);
        assertNull(endResult);
        assertNull(detail);
        assertSame(startResult.getTestRun().getProductVersion().getProduct(), startResult.getTest().getProduct());
        assertEquals(metadata.getProductId(), startResult.getTest().getProduct().getName());
        assertEquals(metadata.getProductVersion(), startResult.getTestRun().getProductVersion().getVersion());
        assertEquals(metadata.getTestId(), startResult.getTest().getName());
        assertEquals(TestType.undetermined, startResult.getTest().getType());
        assertEquals(1, startResult.getTestProperties().size());
        assertEquals(new Double(2.3), startResult.getTestProperties().get("totalTime"));
    }
    
    public void testStartEnd() {
        // invoke the listener with start and stop (but no detail)
        TestProperties props = new TestPropertiesImpl();
        props.put("totalTime", new Double(2.3));
        listener.testRunStarting(metadata, emptyProperties, 20);
        listener.testRunCompleted(metadata, props);
        
        assertNotNull(startResult);
        assertNotNull(endResult);
        assertNull(detail);
        assertSame(endResult.getTestRun().getProductVersion().getProduct(), endResult.getTest().getProduct());
        assertEquals(metadata.getProductId(), endResult.getTest().getProduct().getName());
        assertEquals(metadata.getProductVersion(), endResult.getTestRun().getProductVersion().getVersion());
        assertEquals(metadata.getTestId(), endResult.getTest().getName());
        assertEquals(TestType.undetermined, endResult.getTest().getType());
        assertEquals(1, endResult.getTestProperties().size());
        assertEquals(new Double(2.3), endResult.getTestProperties().get("totalTime"));
    }
    
    public void testStartDetailNoException() {
        // invoke the listener with start and detail (no stop)
        TestProperties props = new TestPropertiesImpl();
        props.put("request", "ThisIsTheRequest");
        props.put("response", new Long(10));
        listener.testRunStarting(metadata, emptyProperties, 20);
        listener.testCallExecuted(executor, metadata, props, 5.0, null);
        
        assertNotNull(startResult);
        assertNull(endResult);
        assertNotNull(detail);
        assertSame(detail.getTestResult(), startResult);
        assertNull(detail.getFailureMessage());
        assertEquals(2, detail.getCallProperties().size());
        assertEquals("ThisIsTheRequest", detail.getCallProperties().get("request"));
        assertEquals(new Long(10), detail.getCallProperties().get("response"));
    }
    
    public void testStartDetailException() {
        // invoke the listener with start and detail (no stop)
        listener.testRunStarting(metadata, emptyProperties, 20);
        Exception e = new Exception("ThisIsTheException");
        e.fillInStackTrace();
        listener.testCallExecuted(executor, metadata, emptyProperties, 5.0, e);
        
        assertNotNull(startResult);
        assertNull(endResult);
        assertNotNull(detail);
        assertSame(detail.getTestResult(), startResult);
        assertEquals(e.getMessage(), detail.getFailureMessage());
        assertEquals(0, detail.getCallProperties().size());
    }
    
    public void testNullProperties() {
        listener.testRunStarting(metadata, null, 20);
        listener.testCallExecuted(executor, metadata, null, 5.0, null);
        listener.testRunCompleted(metadata, null);
        
        assertNotNull(startResult);
        assertNotNull(endResult);
        assertNotNull(detail);
        assertEquals(0, startResult.getTestProperties().size());
        assertEquals(0, endResult.getTestProperties().size());
        assertEquals(0, detail.getCallProperties().size());
    }
    
    /**
     * Make sure the metadata objects are reused between one call and the other
     * (this is good from gc point of view and from hibernate storage pov,
     * hopefully the test will never be so big as to cause an OOM)
     */
    public void testReuseTestRun() {
        listener.testRunStarting(metadata, null, 20);
        listener.testRunCompleted(metadata, null);
        TestResult startResult1 = startResult;
        TestResult endResult1 = endResult;
        listener.testRunStarting(metadata2, null, 20);
        listener.testRunCompleted(metadata2, null);
        TestResult startResult2 = startResult;
        TestResult endResult2 = endResult;
        
        assertNotSame(startResult1, startResult2);
        assertNotSame(endResult1, endResult2);
        assertSame(startResult1.getTestRun(), startResult2.getTestRun());
        assertSame(endResult1.getTestRun(), endResult2.getTestRun());
        assertNotSame(startResult1.getTest(), startResult2.getTest());
        assertNotSame(endResult1.getTest(), endResult2.getTest());
    }
}
