package org.duckhawk.junit3;

import java.util.Map;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

/**
 * Performance and conformance tests may provide extra information about the
 * test being run, such as request, response and the like (for a list of well
 * known properties look into the {@link TestExecutor} javadoc. <br>
 * This interface allows JUnit3 tests to participate in this property gathering
 * operation. A JUnit3 test is supposed to:
 * <ul>
 * <li>store the properties in class fields (each test method is run in a
 * separate instance of the test class)</li>
 * <li>fill in the property map with those field values when
 * {@link #fillProperties(Map)} is called </li>
 * </ul>
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface PropertyTest {
    /**
     * This method is the bridge betweent the test class fields and the
     * {@link org.duckhawk.core.TestExecutor#fillProperties(Map)} method.
     * 
     * @param callProperties
     */
    public void fillProperties(TestProperties callProperties);
}
