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
 * {@link #fillCallProperties(Map)} is called (which will happen after the
 * test/check method sequence is called)</li>
 * </ul>
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface PropertyTest {

    /**
     * Provide the test with the environment properties
     * 
     * @param environment
     */
    public void initEnviroment(TestProperties environment);

    /**
     * This method is the bridge between the test class fields and the
     * {@link org.duckhawk.core.TestExecutor#fillCallProperties(Map)} method.
     * 
     * @param callProperties
     */
    public void fillCallProperties(TestProperties callProperties);
}
