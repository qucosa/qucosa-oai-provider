/*
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.services.RecordService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
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
        classes = {QucosaOaiProviderApplication.class, RecordControllerTest.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {RecordControllerTest.Initializer.class})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class RecordControllerTest {
    private Logger logger = LoggerFactory.getLogger(RecordControllerTest.class);

    @Autowired
    private RecordService recordService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Container
    private static PostgreSQLContainer sqlContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.5")
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

    @Test
    @DisplayName("Find all exists records.")
    @Order(1)
    public void findAll() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/records")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        List<Record> records = om.readValue(response,
                om.getTypeFactory().constructCollectionType(List.class, Record.class));

        assertThat(records).isNotNull();
        assertThat(records.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find all records from to now lastmoddate.")
    @Order(2)
    public void findAllFrom() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/records?metadataPrefix=17&from=2019-01-23")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Collection<Record> records = om.readValue(response,
                om.getTypeFactory().constructCollectionType(Collection.class, Record.class));

        assertThat(records.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find all records from to until lastmoddate.")
    @Order(3)
    public void findAllFromUntil() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/records?metadataPrefix=17&from=2019-01-23&until=2019-01-31")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Collection<Record> records = om.readValue(response,
                om.getTypeFactory().constructCollectionType(Collection.class, Record.class));

        assertThat(records.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find record by uid.")
    @Order(4)
    public void find() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/records/qucosa:32394")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Record record = om.readValue(response, Record.class);

        assertThat(record).isNotNull();
        assertThat(record.getUid()).isEqualTo("qucosa:32394");
    }

    @Test
    @DisplayName("Not record by uid found.")
    @Order(5)
    public void notFound() throws Exception {
        mvc.perform(
                get("/records/qucosa:00000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found record.")));
    }

    @Test
    @DisplayName("Update record by deleted property for mark / undo mark as deleted.")
    @Order(6)
    public void update() throws Exception {
        Record record = recordService.findRecord("uid", "qucosa:32394").iterator().next();
        record.setDeleted(true);

        MvcResult mvcResult = mvc.perform(
                put("/records/qucosa:32394")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(om.writeValueAsString(record)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Record responseObj = om.readValue(response, Record.class);

        assertThat(responseObj).isNotNull();
        assertThat(responseObj.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Update record is not successful because record does not exists.")
    @Order(7)
    public void updateFailed_1() throws Exception {
        Record record = new Record();
        record.setUid("qucosa:00000");

        mvc.perform(
                put("/records/qucosa:00000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(om.writeValueAsString(record)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406 NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot update record.")));
    }

    @Test
    @DisplayName("Update record is not successful because uid parameter and object uid are unequal.")
    @Order(8)
    public void updateFailed_2() throws Exception {
        Record record = new Record();
        record.setUid("qucosa:00000");

        mvc.perform(
                put("/records/qucosa:00001")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(om.writeValueAsString(record)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406 NOT_ACCEPTABLE")))
                .andExpect(jsonPath("$.errorMsg", is("Unequal uid parameter with record object uid.")));
    }

    @Test
    @DisplayName("Delete record from table.")
    @Order(9)
    public void isDeleted() throws Exception {
        MvcResult mvcResult = mvc.perform(
                delete("/records/qucosa:32394")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
        assertThat(Boolean.parseBoolean(response)).isTrue();
    }

    @Test
    @DisplayName("Delete record is not successful because uid parameter is does not exists in racords table.")
    @Order(10)
    public void isNotDeleted() throws Exception {
        mvc.perform(
                delete("/records/qucosa:00000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(new Record())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found record.")));
    }

    @Test
    @DisplayName("Save record trabsport input from camel service.")
    @Order(11)
    public void saveRecordInput() throws Exception {
        mvc.perform(
                post("/records")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(TestData.RECORDS_INPUT))
                .andExpect(status().isOk());
    }
}
