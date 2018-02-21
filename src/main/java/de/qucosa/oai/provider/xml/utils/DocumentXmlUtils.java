package de.qucosa.oai.provider.xml.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class DocumentXmlUtils {
    
    public static String resultXml(Document document) throws IOException, SAXException {
        OutputFormat outputFormat = new OutputFormat(document);
        outputFormat.setOmitXMLDeclaration(true);
        StringWriter stringWriter = new StringWriter();
        XMLSerializer serialize = new XMLSerializer(stringWriter, outputFormat);
        serialize.serialize(document);
        return stringWriter.toString();
    }
    
    public static Element node(InputStream stream) {
        Element element = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Document document = null;
        
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            document = documentBuilder.parse(stream);
            element = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        
        return element;
    }
    
    public static Element node(String input) {
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Element element = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Document document = null;
        
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            document = documentBuilder.parse(stream);
            element = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        
        return element;
    }
}
