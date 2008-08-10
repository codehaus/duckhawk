/*
 *    DuckHawk provides a Performance Testing framework for load
 *    testing applications and web services in an automated and
 *    continuous fashion.
 * 
 *    http://docs.codehaus.org/display/DH/Home
 * 
 *    Copyright (C) 2008 TOPP - http://www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.duckhawk.core;

import java.util.Map;

/**
 * A test properties is just a Map from string keys to Object values. <br>
 * The test executor and the listener can use it to share extra information that
 * is not concerned directly with conformance test results (pass yes/no) nor
 * with performance test results (timings).
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public interface TestProperties extends Map<String, Object> {

}
