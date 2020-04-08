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
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
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
import org.springframework.http.HttpStatus;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties= {"spring.main.allow-bean-definition-overriding=true"},
        classes = {QucosaOaiProviderApplication.class, DisseminationControllerIT.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)

@ContextConfiguration(initializers = {DisseminationControllerIT.Initializer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
class DisseminationControllerIT {
    private List<Dissemination> disseminations = null;

    private Format format;

    @Autowired
    private DisseminationService disseminationService;

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
    public void setUp() throws IOException, SaveFailed {
        disseminations = om.readValue(TestData.DISSEMINATIONS,
                om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
        List<Format> formats = om.readValue(TestData.FORMATS,
                om.getTypeFactory().constructCollectionType(List.class, Format.class));
        format = formatService.saveFormat(formats.get(2));
    }

    /**
     * Value qucosa:32394 is referenced in psql-oai-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Find all disseminations by record uid.")
    @Order(1)
    public void findDisseminations() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/disseminations?uid=qucosa:32394")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Collection<Dissemination> disseminations = om.readValue(response,
                om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));

        assertThat(disseminations).isNotNull();
        assertThat(disseminations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find not disseminations because record uid is wrong / not exists in database.")
    @Order(2)
    public void findNotDisseminations() throws Exception {
        mvc.perform(
                get("/disseminations?uid=qucosa:00000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus", containsString(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found dissemination. UID qucosa:00000 does not exists.")));
    }

    @Test
    @DisplayName("Find dissemination by uid and formatId.")
    @Order(3)
    public void findByTwoParams() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/disseminations?uid=qucosa:32394&formatId=22")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Collection<Dissemination> disseminations = om.readValue(response,
                om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));

        assertThat(disseminations).isNotNull();
        assertThat(disseminations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Save new dissemination.")
    @Order(4)
    public void saveDissemination() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());

        MvcResult mvcResult = mvc.perform(
                post("/disseminations")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Dissemination responseObj = om.readValue(response, Dissemination.class);

        assertThat(responseObj).isNotNull();
        assertThat(responseObj.getDissId()).isNotNull();
    }

    @Test
    @DisplayName("Save dissemination is not successful because record failed.")
    @Order(5)
    public void saveDisseminationWithoutRecord() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());
        dissemination.setRecordId(null);

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.httpStatus", containsString(HttpStatus.NOT_ACCEPTABLE.name())))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because record or format failed.")));
    }

    @Test
    @DisplayName("Save dissemination is not successful because format failed.")
    @Order(6)
    public void saveDisseminationWithoutFormat() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(0L);

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.httpStatus", containsString(HttpStatus.NOT_ACCEPTABLE.name())))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because record or format failed.")));
    }

    /**
     * This test has a dependency to the saveDissemination with order number 2 and is not running as stand alone test.
     * Value qucosa:32394 is referenced in psql-oai-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Save dissemination is not successful because exists in table.")
    @Order(7)
    public void saveDisseminationBecauseExists() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());
        dissemination.setRecordId("qucosa:32394");

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.httpStatus", containsString(HttpStatus.NOT_ACCEPTABLE.name())))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because data row is exists.")));
    }

    /**
     * Value 17 and qucosa:32394 are referenced in psql-oai-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Update dissemination object with delete property for mark object as deleted.")
    @Order(8)
    public void updateDissemination() throws Exception {
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_record = %s AND id_format = %s",
                "qucosa:32394",
                String.valueOf(17));
        dissemination.setDeleted(true);

        MvcResult mvcResult = mvc.perform(
                put("/disseminations/" + dissemination.getRecordId() + "/oai_dc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Dissemination responseObj = om.readValue(response, Dissemination.class);

        assertThat(responseObj).isNotNull();
        assertThat(responseObj.isDeleted()).isTrue();
    }

    /**
     * Value 17 and qucosa:32394 are referenced in psql-oai-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Delete dissemination from table.")
    @Order(9)
    public void deleteDissemination() throws Exception {
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_record = %s AND id_format = %s",
                "qucosa:32394",
                String.valueOf(17));
        MvcResult mvcResult = mvc.perform(
                delete("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
        assertThat(Boolean.parseBoolean(response)).isTrue();
    }

    @AfterAll
    public void shutdownTest() {
        sqlContainer.stop();
    }
}
