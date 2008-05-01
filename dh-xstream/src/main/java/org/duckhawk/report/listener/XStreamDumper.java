package org.duckhawk.report.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.duckhawk.core.TestSuiteListener;
import org.duckhawk.report.model.Product;
import org.duckhawk.report.model.ProductVersion;
import org.duckhawk.report.model.Test;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;
import org.duckhawk.report.model.TestRun;

import com.thoughtworks.xstream.XStream;

public class XStreamDumper extends AbstractModelListener implements
        TestSuiteListener {

    ObjectOutputStream resultsOos;

    ObjectOutputStream detailsOos;

    File root;

    File mainReportFile;

    File detailsRoot;

    XStream xsResults;

    XStream xsDetails;

    public XStreamDumper(File root) {
        this.root = root;
        if (!root.exists())
            if (!root.mkdir())
                throw new IllegalArgumentException("Root directory "
                        + root.getAbsolutePath()
                        + " does not exist and cannot be created either");
        if (root.exists() && !root.isDirectory())
            throw new IllegalArgumentException("Root " + root.getAbsolutePath()
                    + " is supposed to be a directory");

        xsResults = new XStream();
        customizeResultsXStream();

        xsDetails = new XStream();
        customizeDetailsXStream();

        // this shutdown hook is necessary to properly close the files...
        // TODO: try to find a way to specify an event instead of this trick,
        // which is specific to the way junit3 integration is working
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                close();
            }

        });
    }

    /**
     * Customizes XStream dumper for writing down single test calls
     */
    protected void customizeDetailsXStream() {
        xsDetails.setMode(XStream.NO_REFERENCES);
        xsDetails.registerConverter(new TestPropertiesConverter(xsDetails
                .getMapper()));
        xsDetails.alias("TestCallDetail", TestCallDetail.class);
        xsDetails.omitField(TestCallDetail.class, "id");
        xsDetails.omitField(TestCallDetail.class, "testResult");
    }

    /**
     * Customizes XStream dumper for writing down single the full run summary ({@link TestRun}
     * and {@link TestResult} objects)
     */
    protected void customizeResultsXStream() {
        xsResults.setMode(XStream.NO_REFERENCES);
        xsResults.registerConverter(new TestPropertiesConverter(xsResults
                .getMapper()));
        xsResults.registerConverter(new ProductVersionConverter());
        xsResults.alias("TestResult", TestResult.class);
        xsResults.alias("TestInformation", TestRun.class);
        xsResults.omitField(TestResult.class, "id");
        xsResults.omitField(TestResult.class, "testRun");
        xsResults.omitField(TestResult.class, "product");
        xsResults.omitField(Product.class, "id");
        xsResults.omitField(ProductVersion.class, "id");
        xsResults.omitField(Test.class, "id");
        xsResults.omitField(Test.class, "product");
        xsResults.useAttributeFor(Test.class, "name");
        xsResults.useAttributeFor(Test.class, "type");
        xsResults.omitField(TestRun.class, "id");
        xsResults.omitField(TestRun.class, "reference");
    }

    @Override
    protected void testStarting(TestResult result) throws Exception {
        // make sure we prepare if the suite events weren't triggered
        prepareTestSuite(result.getTestRun());

        // let's create the detail report file
        File callReport = new File(detailsRoot, sanitizeFileName(result
                .getTest().getName())
                + ".xml");
        BufferedOutputStream os = new BufferedOutputStream(
                new FileOutputStream(callReport));
        detailsOos = xsDetails.createObjectOutputStream(os, "TestCallDetails");
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^\\w^-^\\.]", ".");
    }

    @Override
    protected void testEnded(TestResult result) throws Exception {
        detailsOos.flush();
        detailsOos.close();

        resultsOos.writeObject(result);
    }

    @Override
    protected void handleDetail(TestCallDetail detail) throws Exception {
        detailsOos.writeObject(detail);
    }

    /**
     * Closes the dumper, makes sure xml files are closed
     */
    public void close() {
        try {
            if (resultsOos != null) {
                resultsOos.close();
                resultsOos = null;
            }

        } catch (IOException e) {
            // I should really get logging going...
        }
        try {
            if (detailsOos != null) {
                detailsOos.close();
                detailsOos = null;
            }
        } catch (IOException e) {
            // same here
        }
    }

    /**
     * Returns the {@link File} for the main xml report. The detailed reports
     * will be in a directory with the same name as the file (minus the
     * <code>.xml</code> extension.
     * 
     * @return
     */
    public File getMainReportFile() {
        return mainReportFile;
    }

    protected void prepareTestSuite(TestRun testRun) throws IOException {
        // if it's the first call, we need to create the general report
        // file and resource
        if (resultsOos == null) {
            String testRunId = testRun.getIdentifier();
            String name = sanitizeFileName(testRunId);

            // build the report object output stream
            mainReportFile = new File(root, name + ".xml");
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(mainReportFile));
            resultsOos = xsResults.createObjectOutputStream(os, "TestSummary");
            resultsOos.writeObject(testRun);

            // prepare the directory to keep all detail files
            detailsRoot = new File(root, name);
            if (!detailsRoot.mkdir())
                throw new IOException(
                        "Could not create the detail reports directory "
                                + detailsRoot.getAbsolutePath());
        }
    }

    @Override
    protected void testSuiteCompleted(TestRun run) throws Exception {
        close();
    }

    @Override
    protected void testSuiteStarting(TestRun run) throws Exception {
        prepareTestSuite(run);
    }

}
