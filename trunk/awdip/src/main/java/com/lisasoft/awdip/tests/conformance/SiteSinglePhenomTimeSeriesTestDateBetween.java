package com.lisasoft.awdip.tests.conformance;

import java.io.IOException;
import java.text.ParseException;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.duckhawk.core.TestExecutor;
import org.xml.sax.SAXException;

import com.lisasoft.awdip.tests.general.AbstractTestDateBetween;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteSinglePhenomTimeSeriesTestDateBetween
extends AbstractTestDateBetween {
    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries"; 
    final static String CONFIG_FILE =
            "/conformance/SiteSinglePhenomTimeSeriesDateBetween.csv";
     
    public SiteSinglePhenomTimeSeriesTestDateBetween()
    throws IOException,  InvalidConfigFileException, ParseException {
       super();
    }
    
    @Override
    protected void checkResponse()
    throws XpathException, SAXException, IOException {
        super.checkResponse();
        
        String response = (String)getCallProperty(TestExecutor.KEY_RESPONSE);
        
        // add additional conformance checks
        AwdipConformanceTest.NumberOfFeaturesCheck(response);
        AwdipConformanceTest.validate(response);  
    }

    @Override
    public String getFeatureTypeName() {
        return FEATURE_TYPE_NAME;
    }

    @Override
    public String getConfigFilename() {
        return CONFIG_FILE;
    }
}
