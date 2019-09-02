/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.api.utils;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

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

    public static XPath xpath(Map<String, String> namespaces) {
        XPath xPath = XPathFactory.newInstance().newXPath();

        if (namespaces != null && !namespaces.isEmpty()) {
            xPath.setNamespaceContext(new SimpleNamespaceContext(namespaces));
        }

        return xPath;
    }

    public static String resultXml(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }

    private static Element node(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        Element element;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(stream);
        element = document.getDocumentElement();

        return element;
    }

    public static Element node(String input) throws IOException, SAXException, ParserConfigurationException {
        return node(IOUtils.toInputStream(input, Charset.defaultCharset()));
    }
}
