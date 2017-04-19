package ru.mirea.ippo.format.xml;

import org.w3c.dom.Document;
import ru.mirea.ippo.format.Property;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public final class StaxDeserializer {

    public static void main(String[] args) throws TransformerException, ParserConfigurationException, XMLStreamException {
        Document document = DomSerializer.createDocument();
        String xml = DomSerializer.toString(document.getDocumentElement());

        List<Property> properties = new LinkedList<>();
        Property property = null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new StringReader(xml));
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String localPart = startElement.getName().getLocalPart();
                if (localPart.equals("Property")) {
                    property = new Property();
                } else if (localPart.equals("Key") && property != null) {
                    event = reader.nextEvent();
                    property.setKey(event.asCharacters().getData());
                } else if (localPart.equals("Value") && property != null) {
                    event = reader.nextEvent();
                    property.setValue(event.asCharacters().getData());
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("Property") && property != null) {
                    properties.add(property);
                    property = null;
                }
            }
        }
        assert properties.size() > 0;
        assert properties.get(0).getKey().equalsIgnoreCase("Key1");
        assert properties.get(0).getValue().equalsIgnoreCase("Value1");
    }
}
