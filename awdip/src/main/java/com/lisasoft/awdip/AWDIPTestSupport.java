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

package com.lisasoft.awdip;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.DefaultTestContext;
import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.core.TestType;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.PropertyNotFoundException;
import com.lisasoft.awdip.util.SetPropertyListener;
import com.lisasoft.awdip.util.TransformHtmlListener;
import com.lisasoft.awdip.util.Util;

/**
 * Central provider for the test context
 * 
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
    public static final String KEY_TESTS_CONFIG_DIR = "testsConfigurationDir";

    // TODO make it a runner parameter
    static String configurationFile = "awdip.properties";
    
    // startup settings for performance tests
    static int perfTimes;

    // startup settings for load tests
    static int loadTimes;
    static int loadNumThreads;
    static int loadRampUp;
    
    /*static Set<TestType> performTests = EnumSet.of(
            TestType.conformance,
            TestType.performance,
            TestType.stress);*/ 
    //static Set<TestType> performTests = EnumSet.of(TestType.performance);
    static Set<TestType> performTests = EnumSet.noneOf(TestType.class);
    

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
     * @param forcePropertyOutput
     *                properties that should definitely make it into the output
     *                (even if originally not set)
     * @return context for AWDIP test suite
     */
    public static TestContext getAwdipContext(String[] forcePropertyOutputLocal) {
        if (context == null) {
            Configuration config = loadAwdipProperties(configurationFile);
            
            perfTimes = config.getInt("perfTimes");
            loadTimes = config.getInt("loadTimes");
            loadNumThreads = config.getInt("loadNumThreads");
            loadRampUp = config.getInt("loadRampUp");
            
            for (String type : config.getStringArray("performTests")) {
                if (type.equals("performance"))
                    performTests.add(TestType.performance);
                else if (type.equals("conformance"))
                    performTests.add(TestType.conformance);
                else if (type.equals("stress"))
                    performTests.add(TestType.stress);
            }
            
            // init xmlunit (from GeoTools' WFSVTestSupport)
            HashMap<String, String> namespaces = new HashMap<String, String>();
            namespaces.put("wfs", "http://www.opengis.net/wfs");
            namespaces.put("ows", "http://www.opengis.net/ows");
            namespaces.put("ogc", "http://www.opengis.net/ogc");
            namespaces.put("gml", "http://www.opengis.net/gml");
            namespaces.put("xs", "http://www.w3.org/2001/XMLSchema");
            namespaces.put("aw", "http://www.water.gov.au/awdip");
            namespaces.put("sa", "http://www.opengis.net/sampling/1.0");
            namespaces.put("om", "http://www.opengis.net/om/1.0");
            namespaces.put("swe", "http://www.opengis.net/swe/1.0.1");
            namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            namespaces.put("cv", "http://www.opengis.net/cv/0.2.1");
            namespaces.put("xlink", "http://www.w3.org/1999/xlink");

            XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(
                    namespaces));

            // setup the environment
            TestProperties environment = new TestPropertiesImpl();
/*            
            environment.put(KEY_HOST, "thor3.adl.ardec.com.au");
            //environment.put(KEY_HOST, "venus.adl.ardec.com.au");
            environment.put(KEY_PORT, 5580);
            environment.put(KEY_GS_PATH, "geoserver2/wfs");
            //environment.put(KEY_GS_PATH, "geoserver/wfs");
*/
            environment.put(KEY_HOST, config.getString("host"));
            environment.put(KEY_PORT, config.getInt("port"));
            environment.put(KEY_GS_PATH, config.getString("geoserverPath"));
            
            environment.put(KEY_TESTS_CONFIG_DIR,
                    config.getString("testsConfigDir"));
            environment.put(KEY_SCHEMA_RPATH,
                    "src/main/resources/schemas/all.xsd");

            /**
             * test call properties that definitely be in the output (set to
             * "null" if not set at all)
             */
            String[] forcePropertyOutputGlobal = new String[] {
                    TestExecutor.KEY_REQUEST, TestExecutor.KEY_RESPONSE };
            String[] forcePropertyOutput;

            forcePropertyOutput = Util.concatStringArrays(
                    forcePropertyOutputGlobal, forcePropertyOutputLocal);
/*
            // cleanup the report directory
            try {
                FileUtils.cleanDirectory(new File("./target/dh-report"));
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
            // setup the context
            context = new DefaultTestContext("AWDIP", "0.1", environment,
                    new PerformanceSummarizer(),
                    new ConformanceSummarizer(true),
                    new SetPropertyListener(forcePropertyOutput),
                    //new PrintStreamListener(true, true),
                    new PrintStreamListener(false, true),
                    new XStreamDumper(
                            new File(config.getString("reportXmlDir"))),
                    new TransformHtmlListener(
                            new File(config.getString("reportHtmlDir"))));
        }
        return context;
    }
    
    /** Load the settings (like server settings, output directories) from the
     * main AWDIP configuration file
     * 
     * @param filename filename of the configuration file 
     */ 
    private static Configuration loadAwdipProperties(String filename) {
        String[] obligatoryProps = new String[]{
                "perfTimes",
                "loadTimes",
                "loadNumThreads", 
                "loadRampUp"
        }; 
        
        try {
            Configuration config = new PropertiesConfiguration(filename);
            
            for (String prop : obligatoryProps) {
                if (!config.containsKey(prop))
                    throw new PropertyNotFoundException("Property \"" + prop
                            + "\" not found in configuration file.");
            }
            return config;
        } catch (PropertyNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(0);
        } catch (ConfigurationException e) {
            System.out.println("Error reading configuration file \""
                    + filename + "\".");
            System.exit(0);            
        }
        return null;
    }
    

    public static int getPerfTimes() {
        return perfTimes;
    }

    public static int getLoadTimes() {
        return loadTimes;
    }

    public static int getLoadNumThreads() {
        return loadNumThreads;
    }

    public static int getLoadRampUp() {
        return loadRampUp;
    }

    public static Set<TestType> getPerformTests() {
        return performTests;
    }

    public static void setPerformTests(Set<TestType> performTests) {
        AWDIPTestSupport.performTests = performTests;
    }
}
