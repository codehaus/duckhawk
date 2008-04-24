package org.duckhawk.report.listener;

import java.io.File;
import java.io.ObjectOutputStream;

import org.duckhawk.report.model.ProductVersion;
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

        xsDetails = new XStream();
        xsDetails.setMode(XStream.NO_REFERENCES);
        xsDetails.omitField(ProductVersion.class, "id");
        xsDetails.omitField(TestResult.class, "testRun");
    }

    @Override
    protected void testStarting(TestResult result) {
        if (resultsOos == null) {
            String name = result.getTestRun().getIdentifier().replaceAll(
                    "[^\\w-\\.]", "_");
            File testRunReport = new File(root, name + ".xml");
//            Hmmm... this does not compile becase we'd need to throw an exception...
            // maybe we should allow listeners to throw exceptions and stop the test in that case?
//            resultsOos = xsResults
//                    .createObjectOutputStream(new BufferedOutputStream(
//                            new FileOutputStream(testRunReport)));

        }
    }

    @Override
    protected void testEnded(TestResult result) {

    }

    @Override
    protected void handleDetail(TestCallDetail detail) {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args) {
        String test = "abc^?*[]dfre  :;,aa";
        System.out.println(test.replaceAll("[^\\w-\\.]", "_"));
    }

}
