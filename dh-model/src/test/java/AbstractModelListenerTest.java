import junit.framework.TestCase;

import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.duckhawk.report.listener.AbstractModelListener;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;

/**
 * @author   Andrea Aime (TOPP)
 */
public class AbstractModelListenerTest extends TestCase {

    public TestCallDetail detail;

    public TestResult endResult;

    public TestResult startResult;

    private TestModelLister listener;

    private TestMetadata metadata;

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
        
    }
    
    public void testStart() {
        TestProperties props = new TestPropertiesImpl();
        props.put("totalTime", new Double(2.3));
        listener.testRunStarting(metadata, props, 20);
        assertNotNull(startResult);
        assertSame(startResult.getTestRun().getProductVersion().getProduct(), startResult.getTest().getProduct());
        assertEquals(metadata.getProductId(), startResult.getTest().getProduct().getName());
        assertEquals(metadata.getProductVersion(), startResult.getTestRun().getProductVersion().getVersion());
        assertEquals(metadata.getTestId(), startResult.getTest().getName());
        assertEquals(TestType.undetermined, startResult.getTest().getType());
        assertEquals(1, startResult.getTestProperties().size());
        assertEquals(new Double(2.3), startResult.getTestProperties().get("totalTime"));
    }
}
