package org.duckhawk.junit3;

import org.duckhawk.core.ConformanceTestRunner;
import org.duckhawk.core.TestContext;
import org.duckhawk.core.TestProperties;
import org.duckhawk.core.TestRunner;

/**
 * A conformance test contains test methods that are supposed to be run just once and used to check if a . Major features of a performance test should be: <ul> <li>each method must be thread safe</li> <li>the total run time of each method must be representative of the operation that one wants to test</li> <li>the test should not be a conformance one, that is, not time should be used in checking the results. If checking is needed anyways (to show errors that do occur only under a high load) remember that the test time time will include both request and checking operations (so it's appropriate for a load test that aims to break the system under test, but not appropriate for a performance measurement, especially if the checks tend to change over time)</li> <li>if the user is interested in them, test properties might be added to the {@link TestProperties}  structure to better document what happened during the run (for example, request and responses)</li> </ul>
 * @author  Andrea Aime (TOPP)
 */
public abstract class ConformanceTest extends AbstractDuckHawkTest {

    public ConformanceTest(TestContext context) {
        super(context);
    }

    protected TestRunner getTestRunner() {
        return new ConformanceTestRunner(context, buildTestExecutor());
    }
}
