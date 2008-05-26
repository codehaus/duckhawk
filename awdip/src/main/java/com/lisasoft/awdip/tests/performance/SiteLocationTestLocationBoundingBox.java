package com.lisasoft.awdip.tests.performance;

import java.io.IOException;

import com.lisasoft.awdip.tests.general.AbstractTestLocationBoundingBox;
import com.lisasoft.awdip.util.InvalidConfigFileException;
import static com.lisasoft.awdip.AWDIPTestSupport.getPerfTimes;

/**
 * Test with growing bounding box. No maxFeatures limit
 * 
 * Rational: A growing of the bounding box corresponds to a user zooming out.
 * How big will be the performance hit the bigger the bounding box gets.
 * 
 * @author vmische
 *
 */
public class SiteLocationTestLocationBoundingBox
extends AbstractTestLocationBoundingBox  {
    final static String FEATURE_TYPE_NAME = "aw:SiteLocation"; 
    final static String CONFIG_FILE =
            "/performance/SiteLocationTestLocationBoundingBox.csv";
     
    public SiteLocationTestLocationBoundingBox()
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

