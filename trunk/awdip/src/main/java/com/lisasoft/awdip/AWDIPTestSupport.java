package com.lisasoft.awdip;

import java.io.File;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

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

    public static TestContext getAwdipContext() {
        if (context == null) {
            // setup the environment
            TestProperties environment = new TestPropertiesImpl();
            environment.put(KEY_HOST, "venus.adl.ardec.com.au");
            environment.put(KEY_PORT, 5580);
            environment.put(KEY_GS_PATH, "geoserver");
            environment.put(KEY_SCHEMA_RPATH, "src/main/resources/schemas/all.xsd");
            
            // setup the context
            context = new TestContext("AWDIP", "0.1", environment,
                    new PerformanceSummarizer(), //
                    new PrintStreamListener(true, true), // 
                    new XStreamDumper(new File("./target/dh-report")));
        }
        return context;
    }

}
