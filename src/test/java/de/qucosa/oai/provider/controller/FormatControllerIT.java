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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.FormatService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
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
import testdata.TestData;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties= {"spring.main.allow-bean-definition-overriding=true"},
        classes = {QucosaOaiProviderApplication.class, FormatControllerIT.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {FormatControllerIT.Initializer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class FormatControllerIT {
    private List<Format> formats = null;

    @Autowired
    private FormatService formatService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

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
        formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
    }

    @Test
    @DisplayName("Find all inserted format rows.")
    @Order(1)
    public void findAll() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        List<Set> data = om.readValue(content, om.getTypeFactory().constructCollectionType(List.class, Format.class));

        assertThat(data).isNotNull();
        assertThat(data.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find format by mdprefix.")
    @Order(2)
    public void findFormat() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/formats/format?mdprefix=oai_dc")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        Format format = om.readValue(content, Format.class);

        assertThat(format).isNotNull();
        assertThat(format.getMdprefix()).isEqualTo("oai_dc");
    }

    @Test
    @DisplayName("Format not found because the mdprefix is wrong.")
    @Order(3)
    public void formatNotFound() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/formats/format?mdprefix=test")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(response, "Cannot found format.");
    }

    @Test
    @DisplayName("Save new format is successful.")
    @Order(4)
    public void saveFormat() throws Exception {
        Format format = formats.get(2);
        MvcResult mvcResult = mvc.perform(
                post("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Format responseFormat = om.readValue(response, Format.class);

        assertThat(responseFormat).isNotNull();
        assertThat(responseFormat.getMdprefix()).isEqualTo("epicur");
    }

    @Test
    @DisplayName("Format cannot save.")
    @Order(5)
    public void formatNotSaved() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();

        MvcResult mvcResult = mvc.perform(
                post("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        Assertions.assertEquals(response, "");
    }

    @Test
    @DisplayName("Update format is successful.")
    @Order(6)
    public void updateFormat() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();
        format.setNamespace("update_ns");
        format.setSchemaUrl("update_url");

        MvcResult mvcResult = mvc.perform(
                put("/formats/oai_dc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Format responseFormat = om.readValue(response, Format.class);
        assertThat(responseFormat).isNotNull();
        assertThat(responseFormat.getNamespace()).isEqualTo("update_ns");
        assertThat(responseFormat.getSchemaUrl()).isEqualTo("update_url");
    }

    @Test
    @DisplayName("Cannot update format object.")
    @Order(7)
    public void formatNotUpdated() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();
        format.setMdprefix("test");

        MvcResult mvcResult = mvc.perform(
                put("/formats/test")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isBadRequest()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isEqualTo("Cannot update format test.");
    }

    @Test
    @DisplayName("Mark format as deleted.")
    @Order(8)
    public void markAsDeleted() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();
        format.setDeleted(true);

        MvcResult mvcResult = mvc.perform(
                put("/formats/oai_dc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Format responseFormat = om.readValue(response, Format.class);

        assertThat(responseFormat).isNotNull();
        assertThat(responseFormat.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Undo mark format as deleted.")
    @Order(9)
    public void markAsDeletedUndo() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();
        format.setDeleted(false);

        MvcResult mvcResult = mvc.perform(
                put("/formats/oai_dc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Format responseFormat = om.readValue(response, Format.class);

        assertThat(responseFormat).isNotNull();
        assertThat(responseFormat.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Hard deleted format from table.")
    @Order(10)
    public void deleteFormat() throws Exception {
        Format format = formatService.find("mdprefix", "oai_dc").iterator().next();
        MvcResult mvcResult = mvc.perform(
                delete("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
        assertThat(Boolean.parseBoolean(response)).isTrue();
    }

    @AfterAll
    public void schutdwonTest() {
        sqlContainer.stop();
    }
}
