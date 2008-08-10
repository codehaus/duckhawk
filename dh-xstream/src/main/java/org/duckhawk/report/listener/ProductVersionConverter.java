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

import org.duckhawk.report.model.ProductVersion;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ProductVersionConverter implements Converter {

    public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        ProductVersion pv = (ProductVersion) source;
        if(pv.getProduct() != null) {
            writer.startNode("name");
            writer.setValue(pv.getProduct().getName());
            writer.endNode();
        }
        writer.startNode("version");
        writer.setValue(pv.getVersion());
        writer.endNode();
    }

    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        throw new UnsupportedOperationException("Unmarshalling still not supported, sorry");
    }

    public boolean canConvert(Class type) {
        return ProductVersion.class.getName().equals(type.getName());
    }

}
