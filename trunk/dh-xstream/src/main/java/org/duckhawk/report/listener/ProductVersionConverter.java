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
