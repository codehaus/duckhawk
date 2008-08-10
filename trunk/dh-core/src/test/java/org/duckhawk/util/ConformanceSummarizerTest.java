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
