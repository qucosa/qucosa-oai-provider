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

package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class OaiPmhControllerGetRecordTest {
    private Logger logger = LoggerFactory.getLogger(OaiPmhControllerGetRecordTest.class);

    @Autowired
    private MockMvc mvc;

    private XPath xPath;

    @BeforeAll
    public void setUp() throws Exception {
        XmlNamespacesConfig namespacesConfig = new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
        xPath = DocumentXmlUtils.xpath(namespacesConfig.getNamespaces());
    }

    @Test
    @DisplayName("OAI_DC: Is xml record not null.")
    @Order(1)
    public void oaiDcXmlNotNull() throws Exception {
        Document xmlRecord = getXmlRecord("oai_dc", "qucosa:30859");
        assertThat(xmlRecord).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Has xml document the GetRecord node.")
    @Order(2)
    public void oaiDcRecordNode() throws Exception {
        Document xmlRecord = getXmlRecord("oai_dc", "qucosa:30859");
        Node node = (Node) xPath.compile("//GetRecord").evaluate(xmlRecord, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("XMetadDissPlus: Is xml record not null.")
    @Order(3)
    public void xmetaDissPlusXmlNotNull() throws Exception {
        Document xmlRecord = getXmlRecord("xmetadissplus", "qucosa:30859");
        assertThat(xmlRecord).isNotNull();
    }

    @Test
    @DisplayName("XMetadDissPlus: Has xml document the GetRecord node.")
    @Order(4)
    public void xmetaDissPlusRecordNode() throws Exception {
        Document xmlRecord = getXmlRecord("xmetadissplus", "qucosa:30859");
        Node node = (Node) xPath.compile("//GetRecord").evaluate(xmlRecord, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Has record not metadata if the status is deleted.")
    @Order(5)
    public void oaiDcHasNotMetatdata() throws Exception {
        Document xmlRecord = getXmlRecord("oai_dc", "qucosa:32394");
        Node node = (Node) xPath.compile("//GetRecord/record/metadata").evaluate(xmlRecord, XPathConstants.NODE);

        assertThat(node.hasChildNodes()).isFalse();
    }

    @Test
    @DisplayName("XMetaDissPlus: Has record not metadata if the status is deleted.")
    @Order(6)
    public void xmetaDissPlusHasNotMetatdata() throws Exception {
        Document xmlRecord = getXmlRecord("xmetadissplus", "qucosa:32394");
        Node node = (Node) xPath.compile("//GetRecord/record/metadata").evaluate(xmlRecord, XPathConstants.NODE);

        assertThat(node.hasChildNodes()).isFalse();
    }

    private Document getXmlRecord(String mdPrefix, String identyfier) throws Exception {
        Document record = null;

        MvcResult mvcResult = mvc.perform(
                get("/oai/GetRecord/" + mdPrefix + "?identyfier=" + identyfier)
                        .contentType(MediaType.APPLICATION_XML_VALUE)).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        if (!response.isEmpty()) {
            record = DocumentXmlUtils.document(new ByteArrayInputStream(response.getBytes("UTF-8")),
                    true);
        }

        return record;
    }
}
