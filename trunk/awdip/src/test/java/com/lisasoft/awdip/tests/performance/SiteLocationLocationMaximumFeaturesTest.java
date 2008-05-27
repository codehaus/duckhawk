package com.lisasoft.awdip.tests.performance;

import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

import java.io.IOException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationMaximumFeatures;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteLocationLocationMaximumFeaturesTest
extends AbstractTestLocationMaximumFeatures {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/performance/SiteLocationTestLocationMaximumFeatures.csv";

    public SiteLocationLocationMaximumFeaturesTest()
    throws IOException,  InvalidConfigFileException {
       super(getPerfTimes());
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
