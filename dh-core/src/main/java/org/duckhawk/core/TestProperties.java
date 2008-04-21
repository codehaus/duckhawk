package org.duckhawk.core;

import java.util.Map;

/**
 * Empty interface for test properties. A test properties is just a Map from
 * string keys to Object values. The test executor and the listener can use it
 * to share extra information that is not concerned directly with conformance
 * test results (pass yes/no) nor with performance test results (timings).
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestProperties extends Map<String, Object> {

}
