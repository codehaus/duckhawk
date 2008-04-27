package org.duckhawk.report.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.duckhawk.report.model.Product;
import org.duckhawk.report.model.ProductVersion;
import org.duckhawk.report.model.Test;
import org.duckhawk.report.model.TestCallDetail;
import org.duckhawk.report.model.TestResult;

import com.thoughtworks.xstream.XStream;

public class XStreamDumper extends AbstractModelListener {

    ObjectOutputStream resultsOos;

    ObjectOutputStream detailsOos;

    File root;

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
        xsResults.setMode(XStream.NO_REFERENCES);
        xsResults.alias("TestResult", TestResult.class);
        xsResults.omitField(TestResult.class, "id");
        xsResults.omitField(TestResult.class, "testRun");
        xsResults.omitField(TestResult.class, "product");
        xsResults.omitField(Product.class, "id");
        xsResults.omitField(ProductVersion.class, "id");
        xsResults.omitField(Test.class, "id");
        xsResults.omitField(Test.class, "product");

        xsDetails = new XStream();
        xsDetails.setMode(XStream.NO_REFERENCES);
        xsDetails.alias("TestCallDetail", TestCallDetail.class);
        xsDetails.omitField(TestCallDetail.class, "id");
        xsDetails.omitField(TestCallDetail.class, "testRun");
        
        // this shutdown hook is necessary to properly close the files...
        // TODO: try to find a way to specify an event instead of this trick, which is
        // specific to the way junit3 integration is working
        Runtime.getRuntime().addShutdownHook(new Thread() {
        
            @Override
            public void run() {
                try {
                    if(resultsOos != null)
                        resultsOos.close();
                } catch(IOException e) {
                    // I should really get logging going...
                }
                try {
                    if(detailsOos != null)
                        detailsOos.close();
                } catch(IOException e) {
                    // same here
                }
            }
        
        });
    }

    @Override
    protected void testStarting(TestResult result) throws Exception {
        // if it's the first call, we need to create the general report
        // file and resource
        if (resultsOos == null) {
            String testRunId = result.getTestRun().getIdentifier();
            String name = sanitizeFileName(testRunId);

            // build the report object output stream
            File testRunReport = new File(root, name + ".xml");
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(testRunReport));
            resultsOos = xsResults.createObjectOutputStream(os, "TestSummary");
            resultsOos.writeObject(result.getTestRun());

            // prepare the directory to keep all detail files
            detailsRoot = new File(root, name);
            if (!detailsRoot.mkdir())
                throw new IOException(
                        "Could not create the detail reports directory "
                                + detailsRoot.getAbsolutePath());
        }

        // let's create the detail report file
        File callReport = new File(detailsRoot, sanitizeFileName(result.getTest().getName()) + ".xml");
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


}
