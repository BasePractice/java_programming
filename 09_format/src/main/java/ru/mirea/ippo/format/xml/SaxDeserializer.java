package ru.mirea.ippo.format.xml;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.mirea.ippo.format.Property;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public final class SaxDeserializer extends DefaultHandler {
    private final List<Property> properties = new LinkedList<>();
    private String thisElement;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        thisElement = qName;
        if ("Property".equals(qName)) {
            properties.add(new Property());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("Property".equals(qName)) {
            thisElement = "";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("Key".equals(thisElement)) {
            Property property = properties.get(properties.size() - 1);
            property.setKey(trim(emptyNull(property.getKey()) + new String(ch, start, length)));
        } else if ("Value".equals(thisElement)) {
            Property property = properties.get(properties.size() - 1);
            property.setValue(trim(emptyNull(property.getValue()) + new String(ch, start, length)));
        }
    }

    private static String emptyNull(String nullable) {
        if (nullable == null)
            return "";
        return nullable;
    }

    private static String trim(String nullable) {
        if (nullable == null)
            return "";
        return nullable.trim();
    }

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SAXException, IOException {
        Document document = DomSerializer.createDocument();
        String xml = DomSerializer.toString(document.getDocumentElement());
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        SaxDeserializer dh = new SaxDeserializer();
        parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), dh);
        assert dh.properties.size() > 0;
        assert dh.properties.get(0).getKey().equalsIgnoreCase("Key1");
        assert dh.properties.get(0).getValue().equalsIgnoreCase("Value1");
    }
}
