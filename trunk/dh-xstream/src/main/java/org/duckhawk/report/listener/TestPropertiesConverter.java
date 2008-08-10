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

package org.duckhawk.report.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.duckhawk.core.TestExecutor;
import org.duckhawk.core.TestProperties;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class TestPropertiesConverter extends MapConverter {

    public TestPropertiesConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return TestProperties.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        TestProperties props = (TestProperties) source;
        List<String> keys = new ArrayList<String>(props.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            writer.startNode("entry");
            writer.addAttribute("key", key);
            Object value = props.get(key);
            
            // for the error summary, we convert the Set<String> into a flat string separated
            // by newlines to avoid the uglyness of the xstream set representation
            if(key.equals(TestExecutor.KEY_ERROR_SUMMARY) && value instanceof Set)
                value = convertErrorMessageSet((Set<String>) value);
            
            if(value != null) {
                String name = mapper().serializedClass(value.getClass());
                writer.addAttribute("type", name);
                context.convertAnother(value);
            }
            writer.endNode();
        }
    }

    private String convertErrorMessageSet(Set<String> value) {
        StringBuilder sb = new StringBuilder();
        for (String message : value) {
            sb.append(message).append("\n");
        }
        if(sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
