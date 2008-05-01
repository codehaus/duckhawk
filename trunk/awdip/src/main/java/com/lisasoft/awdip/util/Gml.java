package com.lisasoft.awdip.util;

public class Gml {
    
    public static String createAndFilterRequest(String typeName, String... filters) {
        StringBuffer request = new StringBuffer();
        request.append("<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:sa=\"http://www.opengis.net/sampling/1.0\" xmlns:aw=\"http://www.water.gov.au/awdip\"  xmlns:ows=\"http://www.opengis.net/ows\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">");
        request.append(" <wfs:Query typeName=\""+typeName+"\">");
        request.append("<ogc:Filter><ogc:And>");

        for (String filter: filters) {
            request.append(filter); 
        }

        request.append("</ogc:And></ogc:Filter></wfs:Query></wfs:GetFeature>");

        return request.toString();
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
