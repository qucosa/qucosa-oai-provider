/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
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

package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties= {"spring.main.allow-bean-definition-overriding=true"},
        classes = {QucosaOaiProviderApplication.class, OaiPmhControllerListIdentifiersIT.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {OaiPmhControllerListIdentifiersIT.Initializer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class OaiPmhControllerListIdentifiersIT {
    @Autowired
    private MockMvc mvc;

    private XPath xPath;

    private static final String VERB = "ListIdentifiers";

    @Container
    private static final PostgreSQLContainer sqlContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.5")
            .withDatabaseName("oaiprovider")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("db/init-tables.sql")
            .withStartupTimeoutSeconds(600);

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            sqlContainer.start();

            TestPropertyValues.of(
                    "spring.datasource.url=" + sqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + sqlContainer.getUsername(),
                    "spring.datasource.password=" + sqlContainer.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public Connection connection() throws SQLException {
            return sqlContainer.createConnection("");
        }
    }

    @BeforeAll
    public void setUp() throws IOException {
        XmlNamespacesConfig namespacesConfig = new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
        xPath = DocumentXmlUtils.xpath(namespacesConfig.getNamespaces());
    }

    @Test
    @DisplayName("If verb parameter not exists in properties verbs config then returns error details object.")
    public void notExistsVerb() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai?verb=ListIdntifers&metadataPrefix=oai_dc")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        Document response = DocumentXmlUtils.document(
                new ByteArrayInputStream(mvcResult.getResponse().getContentAsString().getBytes(StandardCharsets.UTF_8)), true);
        Node errorNode = response.getElementsByTagName("error").item(0);
        String errorCode = errorNode.getAttributes().getNamedItem("code").getTextContent();
        //String errorCode = (String) xPath.compile("//error/@code").evaluate(response, XPathConstants.STRING);

        Assertions.assertEquals("badVerb", errorCode);
    }

    @Test
    @DisplayName("OAI_DC: Has xml document the verb node.")
    public void oaiDcHasVerbNod() throws Exception {
        Document document = xmlResponse("&metadataPrefix=oai_dc");
        Node node = (Node) xPath.compile("//OAI-PMH:" + VERB).evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Has xml the resumtion token node.")
    public void oaiDcHasResumptionTokenNode() throws Exception {
        Document document = xmlResponse("&metadataPrefix=oai_dc");
        Node node = (Node) xPath.compile("//OAI-PMH:resumptionToken").evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("OAI_DC: Return xml from a specific date.")
    public void oaiDcXmlFrom() throws Exception {
        Document document = xmlResponse("&metadataPrefix=oai_dc&from=2019-01-23");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//OAI-PMH:header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    @Test
    @DisplayName("OAI_DC: Return xml from / until a specific date.")
    public void oaiDcXmlFromUntil() throws Exception {
        Document document = xmlResponse("&metadataPrefix=oai_dc&from=2019-01-23&until=2019-01-31");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.evaluate("//OAI-PMH:header", document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    @Test
    @DisplayName("XMetaDissPlus: Has xml document the verb node.")
    public void xMetaDissPlusHasVerbNod() throws Exception {
        Document document = xmlResponse("&metadataPrefix=xmetadissplus");
        Node node = (Node) xPath.compile("//OAI-PMH:" + VERB).evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("XMetaDissPlus: Has xml the resumtion token node.")
    public void xMetaDissPlusHasResumptionTokenNode() throws Exception {
        Document document = xmlResponse("&metadataPrefix=xmetadissplus");
        Node node = (Node) xPath.compile("//OAI-PMH:resumptionToken").evaluate(document, XPathConstants.NODE);
        assertThat(node).isNotNull();
    }

    @Test
    @DisplayName("XMetaDissPlus: Return xml from a specific date.")
    public void xMetaDissPlusXmlFrom() throws Exception {
        Document document = xmlResponse("&metadataPrefix=xmetadissplus&from=2019-01-23");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//OAI-PMH:header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    @Test
    @DisplayName("XMetaDissPlus: Return xml from / until a specific date.")
    public void xMetaDissPlusXmlFromUntil() throws Exception {
        Document document = xmlResponse("&metadataPrefix=xmetadissplus&from=2019-01-23&until=2019-01-31");
        assertThat(document).isNotNull();
        NodeList nodeList = (NodeList) xPath.compile("//OAI-PMH:header").evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList.getLength()).isGreaterThan(0);
    }

    @AfterAll
    public void schutdwonTest() {
        sqlContainer.stop();
    }

    private Document xmlResponse(String params) throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai?verb=" + VERB + params)
                        .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response =  mvcResult.getResponse().getContentAsString();

        return DocumentXmlUtils.document(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)),
                true);
    }
}
