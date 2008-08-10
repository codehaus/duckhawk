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

public class SiteSinglePhenomTimeErrorTest extends AbstractAwdipTest {
    
    static final String DATE_FIELD = "aw:relatedObservation/aw:PhenomenonTimeSeries/om:result/cv:CompactDiscreteTimeCoverage/cv:element/cv:CompactTimeValuePair/cv:geometry"; 
    
    public SiteSinglePhenomTimeErrorTest() {
        super(getAwdipContext());
    }
    
    public void testUnexistentNestedAttributeProperty() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteSinglePhenomTimeSeries ", 2);
        // force in an invalid property request
        body = body.replaceAll("</wfs:Query>",
                "<wfs:PropertyName>aw:RelatedObservation/aw:PhenomenonTimeSeries/om:theMissingProperty</wfs:PropertyName></wfs:Query>");
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing nested attribute (used as a PropertyName).");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
        assertTrue(response.contains("theMissingProperty"));
    }
    
    public void testUnexistentNestedAttributeFilter() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteSinglePhenomTimeSeries", 2, Gml
                .createPropertyFilter("aw:RelatedObservation/aw:PhenomenonTimeSeries/om:theMissingProperty", "notThere"));
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
    
    public void testWrongDateFormatBetween() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteSinglePhenomTimeSeries", 2, Gml
                .createBetweenFilter(DATE_FIELD, "March 13th 2008", "EndOfCentury"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing attribute (used in the filter).");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check (LOUSY, THE DATE IS FETCHED INTO SQL WITHOUT ANY CHECK...)
        // System.out.println(response);
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
    }
    
    public void testWrongDateFormatEquals() throws Exception {
        String body = Gml.createAndFilterMaxFeaturesRequest("aw:SiteSinglePhenomTimeSeries", 2, Gml
                .createPropertyFilter(DATE_FIELD, "March 13th 2008"));
        data.put("body", body);
        putCallProperty(TestExecutor.KEY_REQUEST, body);
        putCallProperty(TestExecutor.KEY_DESCRIPTION,
                "Testing error response for a non existing attribute (used in the filter).");

        // run
        String response = comm.sendRequest(request, data);
        putCallProperty(TestExecutor.KEY_RESPONSE, response);

        // check ((LOUSY, THE DATE IS FETCHED INTO SQL WITHOUT ANY CHECK...)
        // System.out.println(response);
        assertXpathExists("/ows:ExceptionReport/ows:Exception/ows:ExceptionText", response);
    }

}
