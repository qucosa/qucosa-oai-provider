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

package de.qucosa.oai.xml;

import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.api.validators.xml.XmlSchemaValidator;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class XmlValidatorTest {
    private String testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<test><hello></hello><welt /><msg></test>";

    private XmlNamespacesConfig namespacesConfig;

    private XmlSchemaValidator schemaValidator;

    @BeforeAll
    public void init() throws IOException {
        namespacesConfig = new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
        schemaValidator = new XmlSchemaValidator(namespacesConfig);
    }

    @Test
    @DisplayName("Test has xml formed errors.")
    public void foremedTest() {
        Assertions.assertThrows(XmlDomParserException.class, () -> {
            DocumentXmlUtils.document(new ByteArrayInputStream(testXml.getBytes("UTF-8")),
                true);
        });
    }

    @Test
    public void isValidOaiDc() throws XmlDomParserException, XPathExpressionException {
        Document doc = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/xml/oai-dc-test.xml"), true);
        schemaValidator.setXmlDoc(doc);

        Assertions.assertNotNull(schemaValidator.getXmlNode());
        Assertions.assertTrue(schemaValidator.isValid());
    }

    @Test
    public void isValidXMDP() throws XmlDomParserException, XPathExpressionException {
        Document doc = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/xml/xmetadissplus-test.xml"), true);
        schemaValidator.setXmlDoc(doc);

        Assertions.assertNotNull(schemaValidator.getXmlNode());
        Assertions.assertTrue(schemaValidator.isValid());
    }
}
