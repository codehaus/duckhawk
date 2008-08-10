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

package com.lisasoft.awdip.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

/**
 * Utilities for Duckhawk
 * @author vmische
 *
 */
public class Util {
    
    /**
     * Concats two string arrays
     *  
     * @param a array the other one will be appended to
     * @param b array that will be appended to the other one
     * @return
     */
    public static String[] concatStringArrays(String[] a, String[] b) {
        if (a == null && b == null) return new String[0];
        if (a == null) return b;
        if (b == null) return a;
        
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    
    
    /**
     * Converts an hashMap to a nameValuePair array
     * 
     * @param hashMap HashMap of type <String,String> 
     * @return An array of NameValuePairs
     */
    public static NameValuePair[] hashMapToNameValuePairArray(
            Map<String,String> hashMap) {
        NameValuePair[] nameValuePairs = new NameValuePair[hashMap.size()];
        
        int pos = 0;
        for (Iterator<Map.Entry<String,String>> i=hashMap.entrySet().iterator();
                i.hasNext(); pos++) {
            Map.Entry<String,String> pair = i.next();
            nameValuePairs[pos] = new NameValuePair(
                    pair.getKey(), pair.getValue());
        }
        
        return nameValuePairs;
    }
    
    /**
     * Converts an hashMap to a nameValuePair ArrayList
     * 
     * @param hashMap HashMap of type <String,String> 
     * @return An array of NameValuePairs
     */
    public static ArrayList<NameValuePair> hashMapToNameValuePairList(
            HashMap<String,String> hashMap) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        
        int pos = 0;
        for (Iterator<Map.Entry<String,String>> i=hashMap.entrySet().iterator();
                i.hasNext(); pos++) {
            Map.Entry<String,String> pair = i.next();
            nameValuePairs.add(new NameValuePair(
                    pair.getKey(), pair.getValue()));
        }
        
        return nameValuePairs;
    } 
    
    /** swaps to values within an double array
     * 
     * @param array Array to swap
     * @param a swap this value...
     * @param b ...with this one
     */
    public static void swapDouble(double[] array, int a, int b) {
        double tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }
}
