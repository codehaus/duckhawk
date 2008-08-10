/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.lisasoft.awdip.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
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
            
            // setup XSLT parser
            System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
                TransformerFactory tf = TransformerFactory.newInstance();

            // generate general overview report
            URL xsltUrl = getClass()
                    .getResource("/listeners/dh2xhtml_general.xsl");
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
                    .getResource("/listeners/dh2xhtml_details.xsl");
            urlConn = xsltUrl.openConnection();
            xsltStream = urlConn.getInputStream();            
            xsltSource = new StreamSource(xsltStream);
            xsltSource.setSystemId(xsltUrl.getPath());
            
            t = tf.newTransformer(xsltSource);
            // there isn't any outputs only into a sub-directory, not to the
            // main file (the general report won't be overwritten)          
            t.transform(xmlReportSource, transResult);
            
            // copy css file
            File css = new File(htmlDir + "/dh-report.css");
            if (!css.exists())
                FileUtils.copyURLToFile(
                        getClass().getResource("/listeners/dh-report.css"),
                        new File(htmlDir + "/dh-report.css"));
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
