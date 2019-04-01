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
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.config.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.SetService;
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
public class SetControllerTest {
    private Logger logger = LoggerFactory.getLogger(SetControllerTest.class);

    private List<Set> sets = null;

    @Autowired
    private SetService setService;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public void setUp() throws IOException {
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
    }

    @Test
    @DisplayName("Find all inserted sets.")
    @Order(1)
    public void finAll() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        List<Set> data = om.readValue(content, om.getTypeFactory().constructCollectionType(List.class, Set.class));

        assertThat(data).isNotNull();
        assertThat(data.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Find a set object by setspec.")
    @Order(2)
    public void findSet() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/sets/ddc:610")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isNotEmpty();

        Set set = om.readValue(content, Set.class);

        assertThat(set).isNotNull();
        assertThat(set.getSetSpec()).isEqualTo("ddc:610");
    }

    @Test
    @DisplayName("If set not found in table returns a error details object.")
    @Order(3)
    public void setNotFound() throws Exception {
        Set set = sets.get(0);

        mvc.perform(
                get("/sets/" + set.getSetSpec())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404")))
                .andExpect(jsonPath("$.errorMsg", is("Set with setspec " + set.getSetSpec() + " is does not exists.")))
                .andExpect(jsonPath("$.method", is("find")));
    }

    @Test
    @DisplayName("If cannot save set object then returns a error details object.")
    @Order(4)
    public void saveSetNotSuccessful() throws Exception {
        Set set = (Set) setService.find("setspec", "ddc:610").iterator().next();
        mvc.perform(
                post("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save set objects.")))
                .andExpect(jsonPath("$.method", is("save")));
    }

    @Test
    @DisplayName("If the save set process is successful then returns the saved set object.")
    @Order(5)
    public void saveSetIsSuccessful() throws Exception {
        Set set = sets.get(0);
        MvcResult mvcResult = mvc.perform(
                post("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        Set responseSet = om.readValue(response, Set.class);

        assertThat(responseSet).isNotNull();
        assertThat(responseSet.getSetSpec()).isEqualTo(set.getSetSpec());

        setService.delete(responseSet);
    }

    @Test
    @DisplayName("Save an set collection, if this process successful then returns an collection object with saved sets.")
    @Order(6)
    public void saveSetCollectionIsSuccessful() throws Exception {
        MvcResult mvcResult = mvc.perform(
                post("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(sets)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();

        List<Set> responseList = om.readValue(response,
                om.getTypeFactory().constructCollectionType(List.class, Set.class));

        assertThat(responseList).isNotNull();
        assertThat(responseList.size()).isGreaterThan(0);
        assertThat(responseList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Update a exists set object is successful.")
    @Order(7)
    public void updateSet() throws Exception {
        Set set = (Set) setService.find("setspec", "ddc:610").iterator().next();
        set.setSetDescription("This set has a desc now.");

        MvcResult mvcResult = mvc.perform(
                put("/sets/" + set.getSetSpec())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
        Set responseSet = om.readValue(response, Set.class);

        assertThat(responseSet).isNotNull();
        assertThat(responseSet.getSetDescription()).isEqualTo(set.getSetDescription());
    }

    @Test
    @DisplayName("Mark a set object as deleted.")
    @Order(8)
    public void markSetAsDeleted() throws Exception {
        Set set = (Set) setService.find("setspec", "ddc:610").iterator().next();
        set.setDeleted(true);

        MvcResult mvcResult = mvc.perform(
                put("/sets/" + set.getSetSpec())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
        Set responseSet = om.readValue(response, Set.class);

        assertThat(responseSet).isNotNull();
        assertThat(responseSet.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Undo the set delete mark.")
    @Order(9)
    public void undoDeleteMarked() throws Exception {
        Set set = (Set) setService.find("setspec", "ddc:610").iterator().next();
        set.setDeleted(false);

        MvcResult mvcResult = mvc.perform(
                put("/sets/" + set.getSetSpec())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
        Set responseSet = om.readValue(response, Set.class);

        assertThat(responseSet).isNotNull();
        assertThat(responseSet.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Delete set hard from the sets table.")
    @Order(10)
    public void hardDeleteSet() throws Exception {
        Set set = (Set) setService.find("setspec", "ddc:610").iterator().next();

        MvcResult mvcResult = mvc.perform(
                delete("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotEmpty();
        assertThat(Boolean.parseBoolean(response)).isTrue();
    }

    @Test
    @DisplayName("Hard delete set was not successful and returns a error details object.")
    @Order(11)
    public void hardDeleteNotSuccessful() throws Exception {
        Set set = sets.get(0);
        set.setSetSpec("ddc:8000");

        mvc.perform(
                delete("/sets")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(set)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statuscode", is("400")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot hard delete set.")));
    }
}
