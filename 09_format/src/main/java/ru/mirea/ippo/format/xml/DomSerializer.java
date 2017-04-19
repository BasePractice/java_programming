package ru.mirea.ippo.format.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class DomSerializer {
    private static final String NS = "urn://ru-mirea-ippo-format/versions/1.0.0";
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    static Document createDocument() throws ParserConfigurationException {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        Document document = builder.newDocument();

        Element properties = document.createElementNS(NS, "Properties");
        properties.setAttribute("targetNamespace", NS);


        Element property = createProperty(document, "Key1", "Value1");
        document.appendChild(properties);

        properties.appendChild(property);
        return document;
    }

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        Document document = createDocument();
        String content = toString(document.getDocumentElement());
        Files.write(Paths.get("properties.xml"), content.getBytes("UTF-8"));
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        File file = new File("properties.xml");
        file.deleteOnExit();
        Document parsed = builder.parse(file);
        NodeList parsedProperties = parsed.getElementsByTagName("Properties");
        assert parsedProperties.getLength() > 0;
        Node item = parsedProperties.item(0);
        assert item.getNodeName().equals("Properties");
    }

    private static Element createProperty(Document document, String key, String value) {
        Element keyElement = document.createElementNS(NS, "Key");
        keyElement.setTextContent(key);
        Element valueElement = document.createElementNS(NS, "Value");
        valueElement.setTextContent(value);
        Element property = document.createElementNS(NS, "Property");
        property.appendChild(keyElement);
        property.appendChild(valueElement);
        return property;
    }

    static String toString(Element element) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }
}
