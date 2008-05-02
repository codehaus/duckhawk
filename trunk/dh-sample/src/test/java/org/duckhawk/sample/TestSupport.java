package org.duckhawk.sample;

import java.io.File;

import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestPropertiesImpl;
import org.duckhawk.report.listener.XStreamDumper;
import org.duckhawk.util.ConformanceSummarizer;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class TestSupport {
    static TestContext context;

    public static TestContext getContext() {
        if (context == null) {
            TestProperties env = new TestPropertiesImpl();
            env.put("title", "This is a fictous test to show up conformance, "
                    + "performance and stress testing abilities "
                    + "of DuckHawk with the JUnit3 integration");
            context = new TestContext("Math", "1.0", env,
                    new PerformanceSummarizer(), //
                    new ConformanceSummarizer(), //
                    new PrintStreamListener(false, true), // 
                    new XStreamDumper(new File("./target/dh-report")));
        }
        return context;
    }

}
