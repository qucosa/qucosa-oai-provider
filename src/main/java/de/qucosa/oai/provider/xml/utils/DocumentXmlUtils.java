package de.qucosa.oai.provider.xml.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentXmlUtils {
    
    public static <T> Document document(T source, boolean namespaceAware) {
        Document document = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(namespaceAware);
        
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            
            if (source == null) {
                document = documentBuilder.newDocument();
            } else if (source instanceof InputStream) {
                document = documentBuilder.parse((InputStream) source);
            } else if (source instanceof String) {
                document = documentBuilder.parse((String) source);
            } else if (source instanceof InputSource) {
                document = documentBuilder.parse((InputSource) source);
            } else if (source instanceof File) {
                document = documentBuilder.parse((File) source);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        
        return document;
    }
    
    public static XPath xpath() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        
        return xPath;
    }
    
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
