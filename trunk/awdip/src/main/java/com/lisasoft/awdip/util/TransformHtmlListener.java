package com.lisasoft.awdip.util;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestMetadata;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestSuiteListener;
import org.duckhawk.report.listener.XStreamDumper;



/**
 * Transforms the XML produced by the XStreamDumper to XHTML. There it's only
 * usable in conjunction with the XStreamdumper. It runs the transformation
 * at the end of the test suite. This prevents delays while running the suite. 
 * 
 * @author vmische
 *
 */
public class TransformHtmlListener implements TestSuiteListener {
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
    /** Directory where the XStreamDumper created XML report is located */ 
    File xmlDir;
    
    /** Output directory for XHTML report */
    File htmlDir;
    
    public TransformHtmlListener(File xmlDir, File htmlDir) {
        if (!xmlDir.exists())
            throw new IllegalArgumentException("Directory for XML input"
                    + htmlDir.getAbsolutePath()
                    + " does not exist. Have you included the XStreamDumper?");
            
        if (!htmlDir.exists())
            if (!htmlDir.mkdir())
                throw new IllegalArgumentException("Directory for HTML output"
                        + htmlDir.getAbsolutePath()
                        + " does not exist and cannot be created either");
        if (htmlDir.exists() && !htmlDir.isDirectory())
            throw new IllegalArgumentException("Directory for HTML output "
                    + htmlDir.getAbsolutePath()
                    + " is supposed to be a directory");        
        
        this.xmlDir = xmlDir;
        this.htmlDir = htmlDir;
    }

    public String getIdentifier(TestContext context) {
        return context.getProductId() + "-" + context.getProductVersion()
                + "-" + ISO_FORMAT.format(context.getStart());
    }
    
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^\\w^-^\\.]", ".");
    }


    public void testSuiteCompleted(TestContext context) {
        String testRunId = getIdentifier(context);
        String testRunName = sanitizeFileName(testRunId);
        
        try {
            File xmlReportFile = (File) context.getEnvironment().get(XStreamDumper.XML_MAIN_REPORT);
            Source xmlReportSource = new StreamSource(xmlReportFile);

            InputStream xsltStream = getClass()
                    .getResourceAsStream("/listeners/dh2xhtml.xsl");
            Source xsltSource = new StreamSource(xsltStream);
            
            File htmlReport = new File(htmlDir, testRunName + ".html");
            Result transResult = new StreamResult(htmlReport);             
            
            
            System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(xsltSource);

            t.transform(xmlReportSource, transResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }


    public void testSuiteStarting(TestContext context) {
    }


    public void testCallExecuted(TestExecutor executor, TestMetadata metadata,
            TestProperties callProperties, double time, Throwable exception) {
       
    }


    public void testRunCompleted(TestMetadata metadata,
            TestProperties testProperties) {
    }


    public void testRunStarting(TestMetadata metadata,
            TestProperties testProperties, int callNumber) {
    }
}
