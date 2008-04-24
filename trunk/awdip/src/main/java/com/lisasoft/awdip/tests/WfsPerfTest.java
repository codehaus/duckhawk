package com.lisasoft.awdip.tests;



import java.util.HashMap;


import org.duckhawk.junit3.PerformanceTest;
import org.duckhawk.util.PerformanceSummarizer;
import org.duckhawk.util.PrintStreamListener;

import com.lisasoft.awdip.util.Communication;
import com.lisasoft.awdip.util.Request;
import com.lisasoft.awdip.util.Communication.RequestMethod;

public class WfsPerfTest extends PerformanceTest {
    static Communication comm;
    
    String host = "192.168.2.136";
    int port = 8080;
    String geoserverLocation = "geoserver_std";
    

    public WfsPerfTest() {
        super("WfsTest", "1.0", 50, new PerformanceSummarizer(),
                new PrintStreamListener(false, true));
    }

    
    @Override
    protected void setUp() throws Exception {
        comm = new Communication(host, port);
    }

    
    
    public void testGetWfsFeature() {
        String body = "<wfs:GetFeature service=\"WFS\" version=\"1.1.0\" xmlns:topp=\"http://www.openplans.org/topp\" xmlns:wfs=\"http://www.opengis.net/wfs\"   xmlns:ogc=\"http://www.opengis.net/ogc\"   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"   xsi:schemaLocation=\"http://www.opengis.net/wfs                       http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\">   <wfs:Query typeName=\"topp:states\">      <ogc:Filter>        <ogc:FeatureId fid=\"states.3\"/>     </ogc:Filter>     </wfs:Query> </wfs:GetFeature>";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("url", "http://" + host + ":" + port + "/"
                + geoserverLocation + "/wfs");
        data.put("body", body);

        Request request = new Request(RequestMethod.POST, data,
                "/" + geoserverLocation + "/TestWfsPost");
        String response = "";
        try {
            response = comm.sendRequest(request);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}