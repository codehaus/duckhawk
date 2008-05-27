package com.lisasoft.awdip.tests.conformance;

import java.io.IOException;
import java.text.ParseException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationBoundingBoxAndMaximumFeatures;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteLocationTestLocationBoundingBoxAndMaximumFeatures
extends AbstractTestLocationBoundingBoxAndMaximumFeatures {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/conformance/SiteLocationTestLocationBoundingBoxAndMaximumFeatures.csv";
     
    public SiteLocationTestLocationBoundingBoxAndMaximumFeatures()
    throws IOException, InvalidConfigFileException, ParseException {
       super();
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
