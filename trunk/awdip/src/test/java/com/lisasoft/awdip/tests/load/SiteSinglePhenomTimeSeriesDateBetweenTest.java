package com.lisasoft.awdip.tests.load;

import java.io.IOException;
import java.text.ParseException;

import com.lisasoft.awdip.tests.general.AbstractTestDateBetween;
import com.lisasoft.awdip.util.InvalidConfigFileException;

import static com.lisasoft.awdip.AWDIPTestSupport.getLoadNumThreads;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadRampUp;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

public class SiteSinglePhenomTimeSeriesDateBetweenTest
extends AbstractTestDateBetween {
    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries"; 
    final static String CONFIG_FILE =
            "/load/SiteSinglePhenomTimeSeriesTestDateBetween.csv";
     
    public SiteSinglePhenomTimeSeriesDateBetweenTest()
    throws IOException,  InvalidConfigFileException, ParseException {
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
