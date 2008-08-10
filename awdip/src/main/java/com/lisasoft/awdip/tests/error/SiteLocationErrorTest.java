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

package com.lisasoft.awdip.tests.error;

import static com.lisasoft.awdip.AWDIPTestSupport.*;
import static org.custommonkey.xmlunit.XMLAssert.*;

import org.duckhawk.core.TestExecutor;

import com.lisasoft.awdip.AbstractAwdipTest;
import com.lisasoft.awdip.util.Gml;

public class SiteLocationErrorTest extends AbstractAwdipTest {
    public SiteLocationErrorTest() {
        super(getAwdipContext());
    }
    
    public void testNotXML() throws Exception {
        // init
        String body = "Hello there, can you please provide me with the SiteLocation data?";
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a plain text request (instead of valid XML)");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
    }

    public void testNonWellFormedXML() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        // ... let's remove the closing elements
        body = body.replaceAll("</wfs:GetFeature>", "");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for an non well formed XML request.");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
    }

    public void testInvalidElementGetFeature() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        body = body.replaceAll("wfs:GetFeature", "wfs:InvalidElement");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for well formed but schema invalid, "
                        + "replacing wfs:GetFeature with wfs:InvalidElement.");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("InvalidElement"));
    }

    // THIS ONE NOW WAITS FOREVER BECAUSE THE SERVER ON THE OTHER SIDE IS NOT
    // CONFIGURED IN STRICT CITE MODE, MEANING THE VALIDATION IS NOT OCCURRING
    // AND THE FULL DATA SET IS BEING RETURNED
    public void testInvalidElementQuery() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocation");
        body = body.replaceAll("wfs:Query", "wfs:InvalidElementHere");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for well formed but schema invalid, "
                        + "replacing wfs:Query with wfs:InvalidElement.");

        // run
        // REQUEST COMMENTED OUT TO AVOID WAITING FOREVER ON EACH REQUEST
        String response = "THIS REQUEST WASN'T ACTUALLY SENT AS IT WILL RETURN "
        		+ "ALL DATA IF GEOSERVER IS NOT IN STRICT CITE MODE";
        // response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("InvalidElementHere"));
    }

    public void testWrongFeatureType() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("aw:SiteLocationNotThere");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing feature type.");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("SiteLocationNotThere"));
    }

    public void testWrongNamespace() throws Exception {
        // init
        String body = Gml.createAndFilterRequest("notthere:SiteLocation");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing namespace.");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("notthere"));
    }

    public void testUnexistentAttributeFilter() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteLocation", 0, Gml
                .createPropertyFilter("aw:theMissingProperty", "notThere"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing attribute (used in the filter).");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("theMissingProperty"));
    }

    public void testUnexistentAttributeProperty() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteLocation", 2);
        // force in an invalid property request
        body = body.replaceAll("</wfs:Query>",
                "<wfs:PropertyName>aw:theMissingProperty</wfs:PropertyName></wfs:Query>");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing attribute (used as a PropertyName).");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("theMissingProperty"));
    }


    // THIS ONE CANNOT ACTUALLY RUN, SINCE GS IS LENIENT AND DOES NOT THROW AN EXCEPTION
    // FOR THIS KIND OF "INVALID" BBOX. IN FACT FOR SOME SPECS LIKE WCS THIS ONE IS VALID
    // AND SHOULD BE INTERPRETED AS A BBOX THAT SPANS THE DATE LINE
//    public void testInvalidBBox() throws Exception {
//        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteLocation", 2, Gml
//                .createBoundingBoxFilter(new double[] {0, 0, -10, -10}));
//        data.put("body", body);
//        putCallProperty(TestExecutor.KEY_REQUEST, body);
//        putCallProperty(TestExecutor.KEY_DESCRIPTION,
//                "Testing error response for an invalid bbox.");
//
//        // run
//        String response = comm.sendRequest(request, data);
//        putCallProperty(TestExecutor.KEY_RESPONSE, response);
//
//        // check
//        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
//        assertTrue(response.contains("theMissingProperty"));
//    }

}
