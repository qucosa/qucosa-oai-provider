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

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class XmlSchemaValidator {
    private Document xmlDoc;

    private boolean isValid;

    private XPath xPath;

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
    }

    public boolean isValid() {
        checkSchema();
        return isValid;
    }

    private void checkSchema() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        List<String> schemas = xsdSchemas();

        if (schemas != null && schemas.size() > 0) {

            for (String schema : schemas) {

            }
        }
    }

    private List<String> xsdSchemas() {
        List<String> schmemas = new ArrayList<>();

        return schmemas;
    }
}
