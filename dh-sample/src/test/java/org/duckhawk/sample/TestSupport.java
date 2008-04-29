package org.duckhawk.sample;

import java.io.File;

import org.duckhawk.core.TestListener;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class TestSupport {
    static TestListener[] listeners;

    public static TestListener[] getListeners() {
        if (listeners == null) {
            listeners = new TestListener[] {
                    new PerformanceSummarizer(), //
                    new ConformanceSummarizer(), //
                    new PrintStreamListener(false, true), // 
                    new XStreamDumper(new File("./target/dh-report"))
            };
        }
        return listeners;
    }
    
    public static String getProduct() {
        return "Math";
    }
    public static String getVersion() {
        return "1.0";
    }
}
