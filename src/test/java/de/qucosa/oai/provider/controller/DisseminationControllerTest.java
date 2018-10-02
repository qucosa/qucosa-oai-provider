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
import de.qucosa.oai.provider.dao.DisseminationTestDao;
import de.qucosa.oai.provider.dao.FormatTestDao;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DisseminationControllerTest {

    private List<Dissemination> disseminations = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        disseminations = om.readValue(TestData.DISSEMINATIONS, om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class SetControllerTestConfiguration {

        @Bean
        public Dao formatDao() {
            return new FormatTestDao<Format>();
        }

        @Bean
        public FormatService formatApi() {
            FormatService formatService = new FormatService();
            formatService.setDao(formatDao());
            return formatService;
        }

        @Bean
        public Dao disseminationDao() {
            return new DisseminationTestDao();
        }

        @Bean
        public DisseminationService disseminationService() {
            DisseminationService disseminationService = new DisseminationService();
            disseminationService.setDao(disseminationDao());
            return disseminationService;
        }
    }

    @Test
    public void Save_dissemintaion() throws Exception {
        mvc.perform(post("/dissemination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(disseminations.get(0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dissid", is(1)));
    }

    @Test
    public void Save_dissemintaion_not_successful__if_recordid_failed() throws Exception {
        Dissemination dissemination = disseminations.get(0);
        dissemination.setRecordId(null);

        mvc.perform(post("/dissemination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination.")))
                .andExpect(jsonPath("$.statuscode", is("406")));
    }

    @Test
    public void Save_dissemintaion_not_successful__if_formatid_failed() throws Exception {
        Dissemination dissemination = disseminations.get(0);
        dissemination.setFormatId(null);

        mvc.perform(post("/dissemination")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dissemination)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Cannot save dissemination.")))
                .andExpect(jsonPath("$.statuscode", is("406")));
    }

    @Test
    public void Find_disseminations_by_uid() throws Exception {
        mvc.perform(get("/dissemination/oai:example:org:qucosa:55887")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Find_disseminations_by_uid_not_successful() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/dissemination/oai:example:org:qucosa:5588")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        assertThat(res).isEmpty();
    }

    @Test
    public void Mark_disseminations_as_deleted_successful() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadiss")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Mark_disseminations_as_deleted_not_successful_if_has_wrong_format() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetaiss")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMsg", is("Cannot find format.")));
    }

    @Test
    public void Mark_disseminations_as_deleted_not_successful_if_has_wrong_uid() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:5887/xmetadiss")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMsg", is("Cannot find dissemination.")));
    }

    @Test
    public void Undo_Mark_disseminations_as_deleted_successful() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadiss/undo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Undo_Mark_disseminations_as_deleted_not_successful_if_has_wrong_format() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadis/undo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMsg", is("Cannot find format.")));
    }

    @Test
    public void Undo_Mark_disseminations_as_deleted_not_successful_if_has_wrong_uid() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:5887/xmetadiss/undo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMsg", is("Cannot find dissemination.")));
    }

    @Test
    public void Undo_Mark_disseminations_as_deleted_not_successful_if_undo_param_is_wrong() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadiss/und")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMsg", is("The undo param is set, but wrong.")));
    }
}
