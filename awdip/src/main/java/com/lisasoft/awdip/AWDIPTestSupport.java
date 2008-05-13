package com.lisasoft.awdip;

import java.io.File;
import java.util.HashMap;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.SetPropertyListener;
import com.lisasoft.awdip.util.Util;

/**
 * Central provider for the test context
 * @author Andrea Aime (TOPP)
 *
 */
public class AWDIPTestSupport {
    static TestContext context;
    
    public static final String KEY_HOST = "host"; 
    public static final String KEY_PORT = "port";
    public static final String KEY_GS_PATH = "geoserverPath";
    public static final String KEY_SCHEMA_RPATH = "schemaPath";
    public static final String KEY_DESCRIPTION = "description";

    
    /**
     * Setting up the environment for the AWDIP test suite.
     * 
     * @return context for AWDIP test suite
     */    
    public static TestContext getAwdipContext() {
        return getAwdipContext(null);
    }
    
    /**
     * Setting up the environment for the AWDIP test suite.
     * 
     * @param forcePropertyOutput properties that should definitely make it into
     *        the output (even if originally not set)
     * @return context for AWDIP test suite
     */
    public static TestContext getAwdipContext(
            String[] forcePropertyOutputLocal) {
        if (context == null) {
            // init xmlunit (from geotools' WFSVTestSupport)
            HashMap<String, String> namespaces = new HashMap<String, String>();
            namespaces.put("wfs", "http://www.opengis.net/wfs");
            namespaces.put("ows", "http://www.opengis.net/ows");
            namespaces.put("ogc", "http://www.opengis.net/ogc");
            namespaces.put("gml", "http://www.opengis.net/gml");
            namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
            namespaces.put("aw", "http://www.water.gov.au/awdip");
            XMLUnit.setXpathNamespaceContext(
                    new SimpleNamespaceContext(namespaces));             
            
            // setup the environment
            TestProperties environment = new TestPropertiesImpl();
            //environment.put(KEY_HOST, "thor3.adl.ardec.com.au");
            environment.put(KEY_HOST, "venus.adl.ardec.com.au");
            environment.put(KEY_PORT, 5580);
            environment.put(KEY_GS_PATH, "geoserver");
            environment.put(KEY_SCHEMA_RPATH,
                    "src/main/resources/schemas/all.xsd");
            
            /** test call properties that definitely be in the output (set to
             * "null" if not set at all) */ 
            String[] forcePropertyOutputGlobal = new String[]{
                    TestExecutor.KEY_REQUEST,
                    TestExecutor.KEY_RESPONSE};
            String[] forcePropertyOutput;
            
            forcePropertyOutput = Util.concatStringArrays(
                forcePropertyOutputGlobal, forcePropertyOutputLocal);
            
            // setup the context
            context = new TestContext("AWDIP", "0.1", environment,
                    new PerformanceSummarizer(),
                    new ConformanceSummarizer(),
                    new SetPropertyListener(forcePropertyOutput),
                    new PrintStreamListener(true, true), 
                    new XStreamDumper(new File("./target/dh-report")));
        }
        return context;
    }
}
