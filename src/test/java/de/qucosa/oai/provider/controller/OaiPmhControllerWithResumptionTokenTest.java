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
import de.qucosa.oai.provider.config.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class OaiPmhControllerWithResumptionTokenTest {
    private Logger logger = LoggerFactory.getLogger(OaiPmhControllerWithResumptionTokenTest.class);

    @Autowired
    private MockMvc mvc;

    private XPath xPath;

    @BeforeAll
    public void setUp() throws IOException {
        XmlNamespacesConfig namespacesConfig = new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
        xPath = DocumentXmlUtils.xpath(namespacesConfig.getNamespaces());
    }

    @Test
    @DisplayName("Load xml by ListIdentifers verb.")
    public void hasListIdentifiersNode() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListIdentifiers?resumptionToken=c898267ed5a9ad3f656800cf146019822c7ffa33426208d9992f9210fac3a7e9/1")
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).isNotEmpty();

        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(content.getBytes("UTF-8")), true);

        assertThat(document).isNotNull();

        Node listIdentifiers = document.getElementsByTagName("ListIdentifiers").item(0);

        assertThat(listIdentifiers.getNodeName()).isEqualTo("ListIdentifiers");
    }

    @Test
    @DisplayName("Load xml by ListRecords verb.")
    public void hasListRecordsNode() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListRecords?resumptionToken=672be96bd50b710d84b73a8c24b5ff7666f312b8fda556c0c62f75d1135a5619/1")
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(content.getBytes("UTF-8")), true);

        assertThat(document).isNotNull();

        Node listIdentifiers = document.getElementsByTagName("ListRecords").item(0);

        assertThat(listIdentifiers.getNodeName()).isEqualTo("ListRecords");
    }

    @Test
    @DisplayName("Has a record the status deleted then must metadata removed from xml.")
    public void isRecordDeleted() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListRecords?resumptionToken=672be96bd50b710d84b73a8c24b5ff7666f312b8fda556c0c62f75d1135a5619/1")
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(content.getBytes("UTF-8")), true);

        NodeList nodeList = (NodeList) xPath.compile(
                "/OAI-PMH/ListRecords/record/header[@status='deleted']").evaluate(document, XPathConstants.NODESET);

        assertThat(nodeList.getLength()).isGreaterThan(0);

        for (int i = 0; i < nodeList.getLength(); i++) {

            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Node node = nodeList.item(i);
                Node record = node.getParentNode();

                if (record.getNodeName().equals("record")) {
                    Node metadata = (Node) xPath.compile("metadata").evaluate(record, XPathConstants.NODE);

                    assertThat(metadata.hasChildNodes()).isFalse();
                }
            }
        }
    }
}
