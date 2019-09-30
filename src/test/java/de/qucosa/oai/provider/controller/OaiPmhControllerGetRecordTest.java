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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties= {"spring.main.allow-bean-definition-overriding=true"},
        classes = {QucosaOaiProviderApplication.class, OaiPmhControllerGetRecordTest.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {OaiPmhControllerGetRecordTest.Initializer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class OaiPmhControllerGetRecordTest {
    @Autowired
    private MockMvc mvc;

    private XPath xPath;

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
            record = DocumentXmlUtils.document(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)),
                    true);
        }

        return record;
    }

    @AfterAll
    public void schutdwonTest() {
        sqlContainer.stop();
    }
}
