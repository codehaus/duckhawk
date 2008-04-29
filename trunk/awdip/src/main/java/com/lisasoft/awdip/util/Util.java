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
     * Converts an hashMap to a nameValuePair array
     * 
     * @param hashMap HashMap of type <String,String> 
     * @return An array of NameValuePairs
     */
    public static NameValuePair[] hashMapToNameValuePairArray(
            HashMap<String,String> hashMap) {
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
}
