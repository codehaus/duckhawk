package org.duckhawk.util;

import java.util.Set;

import junit.framework.TestCase;

public class ConformanceSummarizerTest extends TestCase {

    public void testEmpty() {
        Set<String> result = new ConformanceSummarizerTestScaffolding().performTests();
        assertEquals(0, result.size());
    }
    
    public void testOne() {
        Set<String> result = new ConformanceSummarizerTestScaffolding(new Exception("ImTheOne")).performTests();
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().contains("ImTheOne"));
    }
    
    public void testMixRepeated() {
        Set<String> result = new ConformanceSummarizerTestScaffolding(null, new Exception("ImTheOne"), null, new Exception("ImTheOne")).performTests();
        assertEquals(1, result.size());
        assertTrue(result.iterator().next().contains("ImTheOne"));
    }
    
    public void testMixTwo() {
        Set<String> result = new ConformanceSummarizerTestScaffolding(null, new Exception("ImTheOne"), null, new Exception("ImTheOtherOne")).performTests();
        assertEquals(2, result.size());
        boolean one = false;
        boolean otherOne = false;
        for (String summary : result) {
            if(summary.contains("ImTheOne"))
               one = true;
            if(summary.contains("ImTheOtherOne"))
                otherOne = true;
        }
        assertTrue(one);
        assertTrue(otherOne);
    }
}
