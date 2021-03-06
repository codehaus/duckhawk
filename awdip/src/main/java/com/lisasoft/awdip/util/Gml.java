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

public class Gml {
    
    
    /**
     * Create a WFS request that requests a single property with optional
     * additional filters. Filter are connected with "and". Number of features
     * that should be returned will be limited by maxFeatures.
     * 
     * @param typeName Name of the feature to request
     * @param maxFeatures Number of maximum features
     * @param filters additional filters 
     * @return A WFS request
     */
    public static String createAndFilterMaxFeaturesRequest(String typeName,
            int maxFeatures, String... filters) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\" xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs ../../wfs/1.1.0/wfs.xsd\"");
        if (maxFeatures>0)
            request.append(" maxFeatures=\"" + maxFeatures + "\"");
        request.append(">");
        request.append(" <wfs:Query typeName=\"" + typeName + "\">");
        
        if (filters.length>0) {
            request.append("<ogc:Filter>");
        
            if (filters.length==1)
                request.append(filters[0]);
            else {
                request.append("<ogc:And>");
                for (String filter: filters) {
                    request.append(filter); 
                }
                request.append("</ogc:And>");
            }
            
            request.append("</ogc:Filter>");
        }
        
        request.append("</wfs:Query></wfs:GetFeature>");
        return request.toString();
    }

    
    /**
     * Create a WFS request that requests a single property with additional
     * filters. Filter are connected with "and". Number of features the should
     * be returned can be limited.
     * 
     * @param typeName Name of the feature to request
     * @param filters additional filters 
     * @return A WFS request
     */
    public static String createAndFilterRequest(String typeName, String... filters) {
        return createAndFilterMaxFeaturesRequest(typeName, 0, filters);
    }
    
    
    /**
     * Create a WFS request that requests a single propert. Number of features
     * that should be returned will be limited by maxFeatures.
     * 
     * @param typeName Name of the feature to request
     * @param maxFeatures Number of maximum features
     * @param filters additional filters 
     * @return A WFS request
     */
    public static String createMaxFeaturesRequest(String typeName,
            int maxFeatures) {
        return createAndFilterMaxFeaturesRequest(typeName, maxFeatures,
                new String[0]);
    }
    
    /**
     * Creates a WFS filter for queries between two values. The boundaries are
     * within the interval (>= and <=)
     * @param property the property
     * @param from start start of the range (date format: yyyy-MM-dd)  
     * @param to end of the range (date format: yyyy-MM-dd)
     * @return WFS filter expression
     */
    public static String createBetweenFilter(String property, String from, String to) {
        return "<ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>"
                + property + "</ogc:PropertyName><ogc:Literal>"
                + from + "</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>"
                + property + "</ogc:PropertyName><ogc:Literal>"
                + to + "</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo>";
    }
    
    /**
     * Creates a WFS filter for queries less or equal than a value (<=)
     * @param property the property
     * @param value value to compare to  
     * @return WFS filter expression
     */
    public static String createLessOrEqualFilter(String property,
            String value) {
        return "<ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>"
                + property + "</ogc:PropertyName><ogc:Literal>"
                + value + "</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo>";
    }    

    /**
     * Creates a WFS filter for queries greater or equal than a value (>=)
     * @param property the property
     * @param value value to compare to  
     * @return WFS filter expression
     */
    public static String createGreaterOrEqualFilter(String property,
            String value) {
        return "<ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>"
                + property + "</ogc:PropertyName><ogc:Literal>"
                + value + "</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo>";
    }    
    
    
    /** Creates the body for a request with a bounding box filter
     * 
     * @param bbox the bounding box that should be used
     * @return
     */
    public static String createBoundingBoxFilter(double[] bbox) {
        return "<ogc:BBOX><ogc:PropertyName>sa:position</ogc:PropertyName><gml:Envelope srsName=\"EPSG:4326\">"
            + "<gml:lowerCorner>"+bbox[0]+" "+bbox[1]+"</gml:lowerCorner>"
            + "<gml:upperCorner>"+bbox[2]+" "+bbox[3]+"</gml:upperCorner>"
            + "</gml:Envelope></ogc:BBOX>";
    }
    
    public static String createPropertyFilter(String property, String literal) {
        return "<ogc:PropertyIsEqualTo>"
            + "<ogc:PropertyName>"+property+"</ogc:PropertyName>"
            + "<ogc:Literal>"+literal+"</ogc:Literal>"
            + "</ogc:PropertyIsEqualTo>";
    }
    

    /**
     * Creates an XPath query that returns how many locations are not within
     * a specified bounding box
     * 
     * @param bbox bounding within the location should be
     * @return XPath expression that will the number of locations that are not
     *     within the specified bounding box 
     */
    public static String createCountNotWithinBoundingBoxXpath(double[] bbox) {
        return "count(/wfs:FeatureCollection/gml:featureMembers/aw:SiteLocation/sa:position/gml:Point/gml:pos[" +
        "number(substring-before(.,' ')) < " + (bbox[0]<bbox[2]?bbox[0]:bbox[2]) + 
        " or number(substring-before(.,' ')) > " + (bbox[0]<bbox[2]?bbox[2]:bbox[0]) + 
        " or number(substring-after(.,' ')) < " + (bbox[1]<bbox[3]?bbox[1]:bbox[3]) + 
        " or number(substring-after(.,' ')) > " + (bbox[1]<bbox[3]?bbox[3]:bbox[1]) + "])";
    }
    
}
