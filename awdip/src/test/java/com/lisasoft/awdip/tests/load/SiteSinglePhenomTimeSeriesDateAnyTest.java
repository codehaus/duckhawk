package com.lisasoft.awdip.tests.load;

import static com.lisasoft.awdip.AWDIPTestSupport.getLoadNumThreads;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadRampUp;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

import java.io.IOException;

import com.lisasoft.awdip.tests.general.AbstractTestDateAny;
import com.lisasoft.awdip.util.InvalidConfigFileException;

public class SiteSinglePhenomTimeSeriesDateAnyTest extends AbstractTestDateAny {
    final static String FEATURE_TYPE_NAME = "aw:SiteSinglePhenomTimeSeries"; 
    final static String CONFIG_FILE =
            "/load/SiteSinglePhenomTimeSeriesTestDateAny.csv";
    
    public SiteSinglePhenomTimeSeriesDateAnyTest()
    throws IOException,  InvalidConfigFileException {
        super(getPerfTimes(), getLoadNumThreads(), getLoadRampUp());
    }
    
    @Override
    public String getConfigFilename() {
        return CONFIG_FILE;
    }

    @Override
    public String getFeatureTypeName() {
        return FEATURE_TYPE_NAME;
    }
}
