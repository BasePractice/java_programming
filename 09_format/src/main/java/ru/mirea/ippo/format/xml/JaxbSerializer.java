package ru.mirea.ippo.format.xml;

import ru.mirea.ippo.format.Properties;
import ru.mirea.ippo.format.Property;

import javax.xml.bind.JAXB;
import java.io.File;

public final class JaxbSerializer {

    public static void main(String[] args) {
        Properties properties = new Properties();
        Property property = new Property();
        property.setKey("Key1");
        property.setValue("Value1");
        properties.getProperty().add(property);
        File file = new File("properties.xml");
//        file.deleteOnExit();
        JAXB.marshal(properties, file);
        Properties unmarshal = JAXB.unmarshal(file, Properties.class);
        Property property1 = unmarshal.getProperty().get(0);
        assert property1.getKey().equals(property.getKey());
        assert property1.getValue().equals(property.getValue());
    }
}
