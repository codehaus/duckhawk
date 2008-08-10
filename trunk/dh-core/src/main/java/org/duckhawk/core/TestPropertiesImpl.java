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

import java.util.HashMap;

/**
 * Sample implementation of the TestProperties interface. Client code must avoid
 * using it and stick with the interface, the implementation details will
 * change.
 * 
 * @TODO Whilst not lots of these maps are created, clear() is a very common
 *       operation, make sure it does not generate significant garbage (i.e.,
 *       use a simple, stable data structure instead of the HashMap one)
 * 
 * @author Andrea Aime (TOPP)
 * 
 */
public class TestPropertiesImpl extends HashMap<String, Object> implements
        TestProperties {

    /**
     * 
     */
    private static final long serialVersionUID = -2787143224307426743L;

}
