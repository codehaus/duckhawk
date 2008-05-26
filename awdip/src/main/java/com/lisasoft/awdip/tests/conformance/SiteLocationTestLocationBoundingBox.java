package com.lisasoft.awdip.tests.conformance;

import java.io.IOException;
import java.text.ParseException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationBoundingBox;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteLocationTestLocationBoundingBox
extends AbstractTestLocationBoundingBox {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/conformance/SiteLocationTestBoundingBox.csv";
     
    public SiteLocationTestLocationBoundingBox()
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
