/**
 * 
 */
package com.lisasoft.ows6.listener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.duckhawk.core.TestContext;
import org.duckhawk.report.listener.XStreamDumper;

/**
 * Extension of the XStreamDumper. This combines XStreamDumper
 * and the TransformHtmlListener, which was written for the
 * AWDIP module.
 * 
 * XStreamDumper generates XML reports for the each test and
 * each test suite.
 * 
 * After a test suite is finished the TransformHtmlListener part
 * transforms the XML report to HTML using XSLT.
 * 
 * @author shansen
 *
 */
public class OWS6TransformHtmlListener extends XStreamDumper {
	
	private static final Logger log = Logger.getLogger(OWS6TransformHtmlListener.class);
	
	public static final String XML_MAIN_REPORT = "xmlMainReportFile";
    /** Output directory for XHTML report */
    File htmlDir;

    
    /**
     * Constructor of the listener gets the folder
     * in which the XML reports are supposed to be
     * stored and the folder for the Html reports.
     * 
     * @param xmlDir XML folder
     * @param htmlDir Html folder
     */
	public OWS6TransformHtmlListener(File xmlDir, File htmlDir) {
		//Construct XStreamDumper stuff
		super(xmlDir);
		
		if (!htmlDir.exists())
            if (!htmlDir.mkdirs()) {
            	log.error("Directory for HTML output"
                        + htmlDir.getAbsolutePath()
                        + " does not exist and cannot be created either");
                throw new IllegalArgumentException("Directory for HTML output"
                        + htmlDir.getAbsolutePath()
                        + " does not exist and cannot be created either");
            }
        if (htmlDir.exists() && !htmlDir.isDirectory()) {
        	log.error("Directory for HTML output "
                    + htmlDir.getAbsolutePath()
                    + " is supposed to be a directory");
            throw new IllegalArgumentException("Directory for HTML output "
                    + htmlDir.getAbsolutePath()
                    + " is supposed to be a directory");
        }
        
        this.htmlDir = htmlDir;
	}
    
    
    public void testSuiteCompleted(TestContext context) {
    	//Finishes XStreamDumper
    	super.testSuiteCompleted(context);
    	
        try {
        	//open the just created XML reports
            File xmlReportFile = (File)context.getEnvironment()
                .get(XML_MAIN_REPORT);
            String xmlReportName = xmlReportFile.getName();
            Source xmlReportSource = new StreamSource(xmlReportFile);
            
            // setup XSLT parser
            System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
                TransformerFactory tf = TransformerFactory.newInstance();

            // generate general overview report
            URL xsltUrl = getClass()
                    .getResource("/listeners/ows62xhtml_general.xsl");
            URLConnection urlConn = xsltUrl.openConnection();
            InputStream xsltStream = urlConn.getInputStream();
            Source xsltSource = new StreamSource(xsltStream);
            xsltSource.setSystemId(xsltUrl.getPath());
            
            File htmlReport = new File(htmlDir, xmlReportName.substring(0,
                    xmlReportName.length()-4) + ".html");
            
            Result transResult = new StreamResult(htmlReport);             
            Transformer t = tf.newTransformer(xsltSource);
            t.transform(xmlReportSource, transResult);

            
            // generate detailed report. Java might run out of memory. This
            // isn't a problem, as no one wants to open such big HTML files
            // in the browser anyway.
            xsltUrl = getClass()
                    .getResource("/listeners/ows62xhtml_details.xsl");
            urlConn = xsltUrl.openConnection();
            xsltStream = urlConn.getInputStream();            
            xsltSource = new StreamSource(xsltStream);
            xsltSource.setSystemId(xsltUrl.getPath());
            
            t = tf.newTransformer(xsltSource);
            // there isn't any outputs only into a sub-directory, not to the
            // main file (the general report won't be overwritten)          
            t.transform(xmlReportSource, transResult);
            
            // copy css file
            File css = new File(htmlDir + "/ows6-report.css");
            
            if (!css.exists())
                FileUtils.copyURLToFile(
                        getClass().getResource("/listeners/ows6-report.css"),
                        new File(htmlDir + "/ows6-report.css"));
        } catch (IOException e) {
        	log.error("IOException in OWS6TransformHtmlListener!", e);
            throw new RuntimeException(e);
		} catch (TransformerException e) {
			log.error("TransformerException in OWS6TransformHtmlListener!", e);
			throw new RuntimeException(e);
		}   
    }
	
}
