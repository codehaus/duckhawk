package org.duckhawk.sample;

import org.duckhawk.junit3.ConformanceTest;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

public class ConformanceTestSample extends ConformanceTest {

    public ConformanceTestSample() {
        super("RandomConformance", "1.0", new PerformanceSummarizer(), new PrintStreamListener(true, false));
    }

    public void testSumFloats() {
        float sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += 0.01f;
        }
        assertEquals(2.0f, sum);
    }


}
