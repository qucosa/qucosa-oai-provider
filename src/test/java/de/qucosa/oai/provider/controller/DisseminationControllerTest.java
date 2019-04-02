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
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.DisseminationService;
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
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DisseminationControllerTest {
    private Logger logger = LoggerFactory.getLogger(DisseminationControllerTest.class);

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

    @BeforeAll
    public void setUp() throws IOException, SaveFailed {
        disseminations = om.readValue(TestData.DISSEMINATIONS,
                om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
        List<Format> formats = om.readValue(TestData.FORMATS,
                om.getTypeFactory().constructCollectionType(List.class, Format.class));
        format = formatService.saveFormat(formats.get(2));
    }

    /**
     * Value qucosa:32394 is referenced in psql-oia-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Find all disseminations by record uid.")
    @Order(1)
    public void findDisseminations() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/disseminations/qucosa:32394")
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
                get("/disseminations/qucosa:00000")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found dissemination. UID qucosa:00000 does not exists.")));
    }

    @Test
    @DisplayName("Save new dissemination.")
    @Order(3)
    public void saveDissemination() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());

        MvcResult mvcResult = mvc.perform(
                post("/disseminations")
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
    @DisplayName("Save dissemination is not succussful because record failed.")
    @Order(4)
    public void saveDisseminationWithoutRecord() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());
        dissemination.setRecordId(null);

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because record or format failed.")));
    }

    @Test
    @DisplayName("Save dissemination is not succussful because format failed.")
    @Order(5)
    public void saveDisseminationWithoutFormat() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(new Long(0));

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because record or format failed.")));
    }

    /**
     * This test has a dependency to the saveDissemination with order number 2 and is not running as stand alone test.
     * Value qucosa:32394 is referenced in psql-oia-provider-test-data.backup file.
     * @throws Exception
     */
    @Test
    @DisplayName("Save dissemination is not succussful because exists in table.")
    @Order(6)
    public void saveDisseminationBecauseExists() throws Exception {
        Dissemination dissemination = disseminations.get(2);
        dissemination.setFormatId(format.getFormatId());
        dissemination.setRecordId("qucosa:32394");

        mvc.perform(
                post("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination because data row is exists.")));
    }

    /**
     * Value 17 and qucosa:32394 are referenced in psql-oia-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Update dissemination object with delete property for mark object as deleted.")
    @Order(7)
    public void updateDissemination() throws Exception {
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format = %s AND id_record = %s",
                String.valueOf(17),
                "qucosa:32394");
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
     * Value 17 and qucosa:32394 are referenced in psql-oia-provider-test-data.backup file.
     */
    @Test
    @DisplayName("Delete dissemination from table.")
    @Order(8)
    public void deleteDissemination() throws Exception {
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format = %s AND id_record = %s",
                String.valueOf(17),
                "qucosa:32394");
        MvcResult mvcResult = mvc.perform(
                delete("/disseminations")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();
        assertThat(Boolean.parseBoolean(response)).isTrue();
    }
}
