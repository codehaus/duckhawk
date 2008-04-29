import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.XMLUnit;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.report.model.Product;
import org.duckhawk.report.model.ProductVersion;
import org.easymock.EasyMock;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XStreamDumperTest extends TestCase {

    XStreamDumper dumper;

    TestExecutor executor;

    TestMetadata metadata;

    TestPropertiesImpl emptyProperties;

    File root;

    private TestPropertiesImpl sampleProperties;

    private TestMetadata metadata2;

    @Override
    protected void setUp() throws Exception {
        root = new File("./target");
        dumper = new XStreamDumper(root);
        executor = EasyMock.createNiceMock(TestExecutor.class);
        metadata = new TestMetadata("ThisIsTheTest", "Product",
                "VapourWareEdition");
        metadata2 = new TestMetadata("ThisIsTheSecondTest", "Product",
                "VapourWareEdition");
        emptyProperties = new TestPropertiesImpl();
        sampleProperties = new TestPropertiesImpl();
        sampleProperties.put(TestExecutor.KEY_AVG_TIME, new Double(12.5));
        sampleProperties.put("test", "testValue");
        sampleProperties.put("nullKey", null);
        sampleProperties.put("pversion", new ProductVersion(new Product(
                "product"), "version"));
    }

    /**
     * Prints the file contents out to the standard output
     * 
     * @param file
     * @throws IOException
     */
    void print(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);
        } finally {
            reader.close();
        }
    }

    public void testNoDetailNewRoot() throws Exception {
        // make sure we have a new clean directory so that the dumper will be
        // forced to create it
        File root = new File("./target/dh-test" + System.currentTimeMillis());
        assertFalse(root.exists());

        // run the dumper
        XStreamDumper d = new XStreamDumper(root);
        d.testRunStarting(metadata, emptyProperties, 25);
        d.testRunCompleted(metadata, sampleProperties);
        d.close();

        // check the dir has been created and the root file is in it
        assertTrue(root.exists());
        assertTrue(root.isDirectory());
        List<File> files = Arrays.asList(root.listFiles());
        assertTrue(files.contains(d.getMainReportFile()));
    }

    public void testNoDetailRootThere() throws Exception {
        // make sure we have a new clean directory so that the dumper will be
        // forced to create it
        File root = new File("./target/fileHere");
        root.delete();
        root.createNewFile();

        // run the dumper
        try {
            XStreamDumper d = new XStreamDumper(root);
            d.testRunStarting(metadata, emptyProperties, 25);
            d.testRunCompleted(metadata, sampleProperties);
            d.close();
            fail("Shouldn't have been able to create the root directory");
        } catch (IllegalArgumentException e) {
            // fine
        }
    }

    public void testDirectoryCannotBeCreated() throws Exception {
        // make sure we have a new clean directory so that the dumper will be
        // forced to create it
        File root = new File("./target/hoolabaloola/dh-test"
                + System.currentTimeMillis());
        assertFalse(root.exists());

        // run the dumper
        try {
            XStreamDumper d = new XStreamDumper(root);
            d.testRunStarting(metadata, emptyProperties, 25);
            fail("It should not be possible to create the root dir");
        } catch (IllegalArgumentException e) {
            // fine
        }
    }

    public void testNoDetail() throws Exception {
        dumper.testRunStarting(metadata, emptyProperties, 25);
        dumper.testRunCompleted(metadata, sampleProperties);
        dumper.close();

        File mainFile = dumper.getMainReportFile();
        // check the file name represents the current test run
        assertTrue(mainFile.getName().contains(metadata.getProductId()));
        assertTrue(mainFile.getName().contains(metadata.getProductVersion()));

        // print(mainFile);
        Document doc = XMLUnit.buildControlDocument(new InputSource(
                new FileInputStream(mainFile)));

        assertXpathEvaluatesTo("1", "count(/TestSummary)", doc);
        assertXpathEvaluatesTo("1", "count(/TestSummary/TestInformation)", doc);
        assertXpathEvaluatesTo(metadata.getProductId(),
                "//productVersion/name", doc);
        assertXpathEvaluatesTo(metadata.getProductVersion(),
                "//productVersion/version", doc);
        assertXpathEvaluatesTo("0", "count(//id)", doc);
        assertXpathEvaluatesTo("1", "count(//TestResult)", doc);
        assertXpathEvaluatesTo("0.0", "//TestResult/time", doc);
        assertXpathEvaluatesTo(metadata.getTestId(), "//TestResult/test/@name",
                doc);
        assertXpathEvaluatesTo("undetermined", "//TestResult/test/@type", doc);
        assertXpathEvaluatesTo("undetermined", "//TestResult/test/@type", doc);
        assertXpathEvaluatesTo("4", "count(//TestResult/testProperties/entry)",
                doc);
        // the pversion one should have two elements below, and then check their
        // values
        // are the one we expect
        assertXpathEvaluatesTo(
                "2",
                "count(//TestResult/testProperties/entry[@key=\"pversion\"]/*)",
                doc);
        assertXpathEvaluatesTo("product",
                "//TestResult/testProperties/entry[@key=\"pversion\"]/name",
                doc);
        assertXpathEvaluatesTo("version",
                "//TestResult/testProperties/entry[@key=\"pversion\"]/version",
                doc);
        assertXpathEvaluatesTo("org.duckhawk.report.model.ProductVersion",
                "//TestResult/testProperties/entry[@key=\"pversion\"]/@type",
                doc);
        // make sure the nullKey entry got an empty entry
        assertXpathEvaluatesTo("0",
                "count(//TestResult/testProperties/entry[@key=\"nullKey\"]/*)",
                doc);
    }

    public void testTwoTests() throws Exception {
        dumper.testRunStarting(metadata, emptyProperties, 25);
        dumper.testRunCompleted(metadata, sampleProperties);
        dumper.testRunStarting(metadata2, emptyProperties, 25);
        dumper.testRunCompleted(metadata2, emptyProperties);
        dumper.close();

        File mainFile = dumper.getMainReportFile();
        // check the file name represents the current test run
        assertTrue(mainFile.getName().contains(metadata.getProductId()));
        assertTrue(mainFile.getName().contains(metadata.getProductVersion()));

        // print(mainFile);
        Document doc = XMLUnit.buildControlDocument(new InputSource(
                new FileInputStream(mainFile)));

        assertXpathEvaluatesTo("1", "count(/TestSummary)", doc);
        assertXpathEvaluatesTo("1", "count(/TestSummary/TestInformation)", doc);
        assertXpathEvaluatesTo("2", "count(//TestResult)", doc);
    }

    public void testDetailTwoCalls() throws Exception {
        Exception e = new Exception("This is an exception!");
        e.fillInStackTrace();
        dumper.testRunStarting(metadata, emptyProperties, 25);
        dumper
                .testCallExecuted(executor, metadata, sampleProperties, 2.5,
                        null);
        dumper.testCallExecuted(executor, metadata, sampleProperties, 3.2, e);
        dumper.testRunCompleted(metadata, emptyProperties);
        dumper.close();

        // make sure the detail dir is there and that we have just one detail
        // report
        String mainFilePath = dumper.getMainReportFile().getAbsolutePath();
        File detailDir = new File(mainFilePath.substring(0, mainFilePath
                .length() - 3));
        assertTrue(detailDir.exists());
        assertEquals(1, detailDir.listFiles().length);

        // open an parse the detail report
        File detailFile = detailDir.listFiles()[0];
        // print(detailFile);
        Document doc = XMLUnit.buildControlDocument(new InputSource(
                new FileInputStream(detailFile)));

        assertXpathEvaluatesTo("2", "count(/TestCallDetails/TestCallDetail)",
                doc);
        assertXpathEvaluatesTo("2.5",
                "//TestCallDetails/TestCallDetail[1]/time", doc);
        assertXpathEvaluatesTo("false",
                "//TestCallDetails/TestCallDetail[1]/failed", doc);
        assertXpathEvaluatesTo("0",
                "count(//TestCallDetails/TestCallDetail[1]/failureMessage)",
                doc);
        assertXpathEvaluatesTo("3.2",
                "//TestCallDetails/TestCallDetail[2]/time", doc);
        assertXpathEvaluatesTo("true",
                "//TestCallDetails/TestCallDetail[2]/failed", doc);
        assertXpathEvaluatesTo(e.getMessage(),
                "//TestCallDetails/TestCallDetail[2]/failureMessage", doc);

        // due to a renamed property we had test run get into the xml output of
        // the
        // detail, thus the following test
        assertXpathEvaluatesTo("0", "count(//testRun)", doc);
    }
    
    public void testDetailSortingKeys() throws Exception {
        dumper.testRunStarting(metadata, emptyProperties, 25);
        dumper.testCallExecuted(executor, metadata, sampleProperties, 2.5,
                        null);
        dumper.testRunCompleted(metadata, sampleProperties);
        dumper.close();

        // make sure the detail dir is there and that we have just one detail
        // report
        String mainFilePath = dumper.getMainReportFile().getAbsolutePath();
        File detailDir = new File(mainFilePath.substring(0, mainFilePath
                .length() - 3));
        assertTrue(detailDir.exists());
        assertEquals(1, detailDir.listFiles().length);

        // open an parse the detail report
        File detailFile = detailDir.listFiles()[0];
        print(detailFile);
        Document doc = XMLUnit.buildControlDocument(new InputSource(
                new FileInputStream(detailFile)));
        
        // now make sure the callProperties keys are sorted in alphabetical order
        List<String> keys = new ArrayList<String>(sampleProperties.keySet());
        Collections.sort(keys);
        assertXpathEvaluatesTo("" + keys.size(), "count(//callProperties/entry)", doc);
        for (int i = 0; i < keys.size(); i++) {
            assertXpathEvaluatesTo(keys.get(i), "//callProperties/entry[" + (i + 1) + "]/@key", doc);
        }
    }

}
