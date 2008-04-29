package org.duckhawk.sample;

import org.duckhawk.junit3.ConformanceTest;

public class SampleConformanceTest extends ConformanceTest {
    
    public SampleConformanceTest() {
        super("RandomConformance", "1.0", TestSupport.getListeners());
    }

    public void testSumFloats() {
        float sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += 0.01f;
        }
        assertEquals(2.0f, sum);
    }


}
