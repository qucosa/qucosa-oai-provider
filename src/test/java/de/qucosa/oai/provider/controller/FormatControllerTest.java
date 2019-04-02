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
import de.qucosa.oai.provider.config.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.FormatService;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import testdata.TestData;

import java.io.IOException;
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
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FormatControllerTest {
    private Logger logger = LoggerFactory.getLogger(FormatControllerTest.class);

    private List<Format> formats = null;

    @Autowired
    private FormatService formatService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

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
                get("/formats/oai_dc")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
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
        mvc.perform(
                get("/formats/test")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found format.")));
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

        mvc.perform(
                post("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save format.")));
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

        mvc.perform(
                put("/formats/test")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot update format.")));
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

    @Test
    @DisplayName("Hard delete format from table is not successful because the mdprefix is wrong.")
    @Order(11)
    public void hardDeleteNotSuccessful() throws Exception {
        Format format = formatService.find("mdprefix", "xmetadissplus").iterator().next();
        format.setMdprefix("test");

        mvc.perform(
                delete("/formats")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(format)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot delete format " + format.getMdprefix() + ".")));
    }
}
