package com.lisasoft.awdip.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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


/**
 * Transforms the XML produced by the XStreamDumper to XHTML. There it's only
 * usable in conjunction with the XStreamdumper. It runs the transformation
 * at the end of the test suite. This prevents delays while running the suite. 
 * 
 * @author vmische
 *
 */
public class TransformHtmlListener implements TestSuiteListener {
    public static final String XML_MAIN_REPORT = "xmlMainReportFile";
    /** Output directory for XHTML report */
    File htmlDir;
    
    public TransformHtmlListener(File htmlDir) {
        if (!htmlDir.exists())
            if (!htmlDir.mkdirs())
                throw new IllegalArgumentException("Directory for HTML output"
                        + htmlDir.getAbsolutePath()
                        + " does not exist and cannot be created either");
        if (htmlDir.exists() && !htmlDir.isDirectory())
            throw new IllegalArgumentException("Directory for HTML output "
                    + htmlDir.getAbsolutePath()
                    + " is supposed to be a directory");

        this.htmlDir = htmlDir;
    }

    public void testSuiteCompleted(TestContext context) {
        try {
            File xmlReportFile = (File)context.getEnvironment()
                .get(XML_MAIN_REPORT);
            String xmlReportName = xmlReportFile.getName();
            Source xmlReportSource = new StreamSource(xmlReportFile);

            InputStream xsltStream = getClass()
                    .getResourceAsStream("/listeners/dh2xhtml.xsl");
            Source xsltSource = new StreamSource(xsltStream);
            
            File htmlReport = new File(htmlDir, xmlReportName.substring(0,
                    xmlReportName.length()-4) + ".html");
            
            Result transResult = new StreamResult(htmlReport);             
            System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(xsltSource);

            t.transform(xmlReportSource, transResult);
            
            // copy css file
            saveStream(getClass()
                    .getResourceAsStream("/listeners/dh-report.css"),
                    htmlDir + "/dh-report.css");
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }

    
    /**
     * Saves Input stream to file
     * @throws IOException 
     * @throws Exception 
     */
    private static void saveStream(InputStream src, String dest)
    throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        byte[] buf = new byte[1024];
        int i = 0;
        
        while ((i = src.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }            
     
        fos.close();
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
