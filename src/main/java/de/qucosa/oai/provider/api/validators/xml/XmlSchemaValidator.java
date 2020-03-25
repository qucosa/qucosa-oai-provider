/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.api.validators.xml;

import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.xmlunit.util.IterableNodeList.asList;

public class XmlSchemaValidator {
    private Document xmlDoc;

    private boolean isValid = true;

    private XPath xPath;

    private Node xmlNode;

    public XmlSchemaValidator(XmlNamespacesConfig xmlNamespacesConfig) {
        xPath = DocumentXmlUtils.xpath(xmlNamespacesConfig.getNamespaces());
    }

    public void setXmlDoc(String data) throws UnsupportedEncodingException, XmlDomParserException {
        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(data.getBytes("UTF-8")), true);
        setXmlDoc(document);
    }

    public void setXmlDoc(Document xmlDoc) {
        this.xmlDoc = xmlDoc;
        setXmlNode(this.xmlDoc.getDocumentElement());
    }

    public void setXmlNode(Node xmlNode) {
        this.xmlNode = xmlNode;
    }

    public boolean isValid() throws XPathExpressionException, IOException {
        checkSchema();
        return isValid;
    }

    public Node getXmlNode() {
        return xmlNode;
    }

    private void checkSchema() throws XPathExpressionException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        List<String> schemas = xsdSchemas();

        if (schemas != null && schemas.size() > 0) {

            for (String schemaUrl : schemas) {
                try {

                    if (!ValidateXsdSchemas.validateSchemas.get(schemaUrl)) {
                        loadFinalSchema(schemaUrl);
                    }

                    /*if (ValidateXsdSchemas.validateSchemas.get(schemaUrl)) {
                        Schema schema = schemaFactory.newSchema(new URL(schemaUrl));
                        Validator validator = schema.newValidator();
                        Source xmlSource = new DOMSource(xmlDoc);
                        validator.validate(xmlSource);
                    }*/
                } catch (SAXException | MalformedURLException | URISyntaxException | TransformerException e) {
                    throw new RuntimeException("URL or XML error.", e);
                }
            }
        }
    }

    private List<String> xsdSchemas() throws XPathExpressionException {
        List<String> schmemas = new ArrayList<>();
        String schemaLocationAttr = (String) xPath.compile("@xsi:schemaLocation")
                .evaluate(xmlNode, XPathConstants.STRING);
        List<String> schemaValues = new ArrayList<>(Arrays.asList(schemaLocationAttr.split(" ")));
        schemaValues.removeIf(item -> item == null || "".equals(item));

        for (String val : schemaValues) {

            if (val.contains(".xsd")) {
                schmemas.add(val);
            }
        }

        return schmemas;
    }

    private void loadFinalSchema(String schemaUrl) throws MalformedURLException, URISyntaxException, XmlDomParserException, TransformerException, XPathExpressionException {
        Document document = DocumentXmlUtils.document(schemaUrl, true);
        DocumentXmlUtils.resultXml(document);
        NodeList imports = document.getElementsByTagName("import");
        NodeList includes = document.getElementsByTagName("include");

        iterateImports(imports);
    }

    private void iterateImports(NodeList nodeList) {

        if (nodeList.getLength() > 0) {

            for (Node n : asList(nodeList)) {
                String[] locations = n.getAttributes().getNamedItem("schemaLocation").getTextContent().split(" ");

                if (locations.length > 0) {

                    for (String loc : Arrays.asList(locations)) {
                        try {
                            StringBuilder content = new StringBuilder();
                            URL url = new URL(loc);
                            URLConnection urlConnection = url.openConnection();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String line;

                            // read from the urlconnection via the bufferedreader
                            while ((line = reader.readLine()) != null) {
                                content.append(line + "\n");
                            }

                            reader.close();
                            content.toString();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
