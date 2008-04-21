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
