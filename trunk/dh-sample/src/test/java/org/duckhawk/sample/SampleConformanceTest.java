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

package org.duckhawk.sample;

import org.duckhawk.junit3.ConformanceTest;

public class SampleConformanceTest extends ConformanceTest {
    
    public SampleConformanceTest() {
        super(TestSupport.getContext());
    }

    public void testSumFloats() {
        float sum = 0;
        for (int i = 0; i < 200; i++) {
            sum += 0.01f;
        }
        assertEquals(2.0f, sum);
    }


}
