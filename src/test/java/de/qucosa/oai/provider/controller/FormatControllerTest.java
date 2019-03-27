/**
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
import de.qucosa.oai.provider.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
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
        om = new ObjectMapper();
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

//
//    @Test
//    public void Save_single_format_object() throws Exception {
//        Format format = formats.get(0);
//        mvc.perform(post("/formats")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(format)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.formatid", is(1)));
//    }
//
//    @Test
//    public void Save_single_format_object_not_successful() throws Exception {
//        Format format = formats.get(0);
//        format.setIdentifier(1);
//        mvc.perform(post("/formats")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(format)))
//                .andExpect(status().isNotAcceptable())
//                .andExpect(jsonPath("$.statuscode", is("406")))
//                .andExpect(jsonPath("$.errorMsg", is("Cannot save format.")));
//    }
//
//    @Test
//    public void Save_format_collection() throws Exception {
//        mvc.perform(post("/formats")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(formats)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)));
//    }
//
//    @Test
//    public void Update_format() throws Exception {
//        Format format = formats.get(0);
//        format.setNamespace("mist");
//        mvc.perform(put("/formats/oai_dc")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(format)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.namespace", is("mist")));
//    }
//
//    @Test
//    public void Update_format_not_successful() throws Exception {
//        Format format = formats.get(0);
//        format.setNamespace("mist");
//        mvc.perform(put("/formats/oai_c")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(format)))
//                .andExpect(status().isNotAcceptable())
//                .andExpect(jsonPath("$.statuscode", is("406")))
//                .andExpect(jsonPath("$.errorMsg", is("Cannot update format.")));
//    }
//
}
