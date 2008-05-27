package com.lisasoft.awdip.tests.load;

import java.io.IOException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationBoundingBox;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadNumThreads;
import static com.lisasoft.awdip.AWDIPTestSupport.getLoadRampUp;

/**
 * Test with growing bounding box. No maxFeatures limit
 * 
 * Rational: A growing of the bounding box corresponds to a user zooming out.
 * How big will be the performance hit the bigger the bounding box gets.
 * 
 * @author vmische
 *
 */
public class SiteLocationLocationBoundingBoxTest
extends AbstractTestLocationBoundingBox  {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/load/SiteLocationTestLocationBoundingBox.csv";
     
    public SiteLocationLocationBoundingBoxTest()
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

