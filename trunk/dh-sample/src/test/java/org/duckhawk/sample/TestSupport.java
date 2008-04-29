package org.duckhawk.sample;

import java.io.File;

import org.duckhawk.core.TestListener;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class TestSupport {
    static TestListener[] listeners;

    public static TestListener[] getListeners() {
        if (listeners == null) {
            listeners = new TestListener[] {
                    new PerformanceSummarizer(), //
                    new PrintStreamListener(false, true), // 
                    new XStreamDumper(new File("./target/dh-report"))
            };
        }
        return listeners;
    }
}
