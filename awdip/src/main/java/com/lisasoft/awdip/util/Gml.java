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
        }
        
        request.append("</ogc:Filter></wfs:Query></wfs:GetFeature>");
        return request.toString();
    }

    
    /**
     * Create a WFS request that requests a single property with additional
     * filters. Filter are connected with "and". Number of features the should
     * be returned can be limited.
     * 
     * @param typeName Name of the feature to request
     * @param maxFeatures Number of maximum features
     * @return A WFS request
     */
    public static String createAndFilterRequest(String typeName, String... filters) {
        return createAndFilterMaxFeaturesRequest(typeName, 0, filters);
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
    

    
}
