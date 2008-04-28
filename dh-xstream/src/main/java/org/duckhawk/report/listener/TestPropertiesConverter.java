package org.duckhawk.report.listener;

import java.util.Map.Entry;

import org.duckhawk.core.TestProperties;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
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
        for (Entry<String, Object> entry : props.entrySet()) {
            writer.startNode("entry");
            writer.addAttribute("key", entry.getKey());
            Object value = entry.getValue();
            if(value != null) {
                String name = mapper().serializedClass(value.getClass());
                writer.addAttribute("type", name);
                context.convertAnother(value);
            }
            writer.endNode();
        }
    }

}
