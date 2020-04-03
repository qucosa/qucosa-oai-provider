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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlSchemaValidator {
    private Document xmlDoc;

    private boolean isValid = true;

    private XPath xPath;

    private Node xmlNode;

    private String format;

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

    public void setFormat(String format) {
        this.format = format;
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
        String schemaUrl= xsdSchema();
        Schema schema;

        if (schemaUrl.isEmpty()) {
            throw new RuntimeException("Schema is empty.");
        }

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
        } catch (IOException | SAXException e) {

        }
    }

    private String xsdSchema() throws XPathExpressionException {
        List<String> schmemas = new ArrayList<>();
        String schemaLocationAttr = (String) xPath.compile("@xsi:schemaLocation")
                .evaluate(xmlNode, XPathConstants.STRING);
        String schema = "";

        for (String val : Arrays.asList(schemaLocationAttr.split(" "))) {

            if (val.contains(format + ".xsd")) {
                schema = val;
            }
        }

        return schema;
    }
}
