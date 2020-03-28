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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        schemaFactory.setResourceResolver(new RedirectResolver());
        List<String> schemas = xsdSchemas();

        if (schemas != null && schemas.size() > 0) {

            for (String schemaUrl : schemas) {

                try {
                    iterateSchemas(schemaUrl, schemaFactory);
                } catch (SAXException e) {
                    isValid = false;
                    System.out.println("Error-Schema ("+schemaUrl+"): " + e.getMessage());
                }
            }
        }
    }

    private void iterateSchemas(String schemaUrl, SchemaFactory schemaFactory) throws MalformedURLException, SAXException {
        Schema schema;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(schemaUrl);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream in = httpEntity.getContent();
                schema = schemaFactory.newSchema(new StreamSource(in));
                EntityUtils.consume(httpEntity);
                Validator validator = schema.newValidator();
                Source xmlSource = new DOMSource(xmlDoc);
                validator.validate(xmlSource);
            }
        } catch (IOException e) {

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
}
