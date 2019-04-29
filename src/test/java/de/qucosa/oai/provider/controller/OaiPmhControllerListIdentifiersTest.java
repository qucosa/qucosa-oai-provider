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
import javax.xml.xpath.XPathExpressionException;
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
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OaiPmhControllerListIdentifiersTest {
    private Logger logger = LoggerFactory.getLogger(OaiPmhControllerListIdentifiersTest.class);

    @Autowired
    private MockMvc mvc;

    private XPath xPath;

    private static final String VERB = "ListIdentifiers";

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
    @DisplayName("OAI_DC: Has xml document the verb node.")
    public void oaiDcHasVerbNod() throws Exception {
        Document document = xmlResponse("/oai_dc");
        Node node = (Node) xPath.compile("//" + VERB).evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Has xml the resumtion token node.")
    public void oaiDcHasResumptionTokenNode() throws Exception {
        Document document = xmlResponse("/oai_dc");
        Node node = (Node) xPath.compile("//resumptionToken").evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Return xml from a specific date.")
    public void oaiDcXmlFrom() throws Exception {
        Document document = xmlResponse("/oai_dc/2019-01-23");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
        assertThat(nodeList.getLength()).isEqualTo(10);
    }

    @Test
    @DisplayName("OAI_DC: Return xml from / until a specific date.")
    public void oaiDcXmlFromUntil() throws Exception {
        Document document = xmlResponse("/oai_dc/2019-01-23/2019-01-31");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    @Test
    @DisplayName("XMetaDissPlus: Has xml document the verb node.")
    public void xMetaDissPlusHasVerbNod() throws Exception {
        Document document = xmlResponse("/xmetadissplus");
        Node node = (Node) xPath.compile("//" + VERB).evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("XMetaDissPlus: Has xml the resumtion token node.")
    public void xMetaDissPlusHasResumptionTokenNode() throws Exception {
        Document document = xmlResponse("/xmetadissplus");
        Node node = (Node) xPath.compile("//resumptionToken").evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("XMetaDissPlus: Return xml from a specific date.")
    public void xMetaDissPlusXmlFrom() throws Exception {
        Document document = xmlResponse("/xmetadissplus/2019-01-23");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
        assertThat(nodeList.getLength()).isEqualTo(10);
    }

    @Test
    @DisplayName("XMetaDissPlus: Return xml from / until a specific date.")
    public void xMetaDissPlusXmlFromUntil() throws Exception {
        Document document = xmlResponse("/xmetadissplus/2019-01-23/2019-01-31");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    private Document xmlResponse(String params) throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/" + VERB + "/" + params)
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response =  mvcResult.getResponse().getContentAsString();

        return DocumentXmlUtils.document(new ByteArrayInputStream(response.getBytes("UTF-8")),
                true);
    }
}