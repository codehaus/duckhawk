package org.duckhawk.report.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            if(value != null) {
                String name = mapper().serializedClass(value.getClass());
                writer.addAttribute("type", name);
                context.convertAnother(value);
            }
            writer.endNode();
        }
    }

}
