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
import org.springframework.test.annotation.DirtiesContext;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties", properties = {"records.pro.page=10"})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OaiPmhControllerWithoutResumptionTokenTest {
    private Logger logger = LoggerFactory.getLogger(OaiPmhControllerWithoutResumptionTokenTest.class);

    @Autowired
    private MockMvc mvc;

    private XPath xPath;

    @BeforeAll
    public void setUp() throws IOException {
        XmlNamespacesConfig namespacesConfig = new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
        xPath = DocumentXmlUtils.xpath(namespacesConfig.getNamespaces());
    }

    @Test
    @DisplayName("If verb parameter not exists in properties verbs config then retirns error details object.")
    public void notExistsVerb() throws Exception {
        mvc.perform(
                get("/oai/ListIdentifers/oai_dc")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statuscode", is("400")))
                .andExpect(jsonPath("$.errorMsg", is("The verb (ListIdentifers) is does not exists in OAI protocol.")));
    }

    @Test
    @DisplayName("Load xml by ListIdentifers verb.")
    public void hasListIdentifiersNode() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListIdentifiers/oai_dc")
                .accept(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE))
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
    @DisplayName("Load xml data with date from parameter.")
    public void listFromToNow() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListIdentifiers/oai_dc/2019-01-23")
                        .accept(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).isNotEmpty();
    }

    @Test
    @DisplayName("Load xml data with date from parameter.")
    public void listFromToUntil() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListIdentifiers/oai_dc/2019-01-23/2019-01-31")
                        .accept(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content).isNotEmpty();
    }

    @Test
    @DisplayName("Load xml by ListRecords verb.")
    public void hasListRecordsNode() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListRecords/oai_dc")
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
    @DisplayName("Returns list of formats.")
    public void getListMetadataFormats() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListMetadataFormats")
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();

        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(response.getBytes("UTF-8")), true);
        assertThat(document).isNotNull();

        Node listMetadataFormats = document.getElementsByTagName("ListMetadataFormats").item(0);
        assertThat(listMetadataFormats.getNodeName()).isEqualTo("ListMetadataFormats");
    }

    @Test
    @DisplayName("Return identify configuration xml.")
    public void getIdentify() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/Identify")
                        .accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
    }

    @Test
    @DisplayName("Has a record the status deleted then must metadata removed from xml.")
    public void isRecordDeletedInList() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListRecords/oai_dc")
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

    @Test
    @DisplayName("Load xml by ListSets node and check if verb node is exists.")
    public void listSets() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListSets")
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        Document document = DocumentXmlUtils.document(
                new ByteArrayInputStream(content.getBytes("UTF-8")), true);

        assertThat(document).isNotNull();

        Node listIdentifiers = document.getElementsByTagName("ListSets").item(0);

        assertThat(listIdentifiers.getNodeName()).isEqualTo("ListSets");

    }
}
