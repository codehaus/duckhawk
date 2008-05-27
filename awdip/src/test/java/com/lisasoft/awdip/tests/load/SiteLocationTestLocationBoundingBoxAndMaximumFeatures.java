package com.lisasoft.awdip.tests.load;

import static com.lisasoft.awdip.AWDIPTestSupport.getLoadNumThreads;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadRampUp;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

import java.io.IOException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationBoundingBoxAndMaximumFeatures;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteLocationTestLocationBoundingBoxAndMaximumFeatures
extends AbstractTestLocationBoundingBoxAndMaximumFeatures {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/load/SiteLocationTestLocationBoundingBoxAndMaximumFeatures.csv";

    public SiteLocationTestLocationBoundingBoxAndMaximumFeatures()
    throws IOException,  InvalidConfigFileException {
        super(getPerfTimes(), getLoadNumThreads(), getLoadRampUp());
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
