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
import de.qucosa.oai.provider.dao.FormatTestDao;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Format;
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
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FormatControllerTest {

    private List<Format> formats = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class FormatControllerTestConfiguration {
        @Bean
        public Dao formatDao() {
            return new FormatTestDao<Format>();
        }

        @Bean
        public FormatService formatService() {
            FormatService formatService = new FormatService();
            formatService.setDao(formatDao());
            return formatService;
        }
    }

    @Test
    public void Save_single_format_object() throws Exception {
        Format format = formats.get(0);
        mvc.perform(post("/formats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(format)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formatid", is(1)));
    }

    @Test
    public void Save_single_format_object_not_successful() throws Exception {
        Format format = formats.get(0);
        format.setIdentifier(1);
        mvc.perform(post("/formats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(format)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot save format.")));
    }

    @Test
    public void Save_format_collection() throws Exception {
        mvc.perform(post("/formats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(formats)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void Update_format() throws Exception {
        Format format = formats.get(0);
        format.setNamespace("mist");
        mvc.perform(put("/formats/oai_dc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(format)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namespace", is("mist")));
    }

    @Test
    public void Update_format_not_successful() throws Exception {
        Format format = formats.get(0);
        format.setNamespace("mist");
        mvc.perform(put("/formats/oai_c")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(format)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.statuscode", is("406")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot update format.")));
    }

    @Test
    public void Find_format_by_mdprefix_successful() throws Exception {
        mvc.perform(get("/formats/oai_dc")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mdprefix", is("oai_dc")));
    }

    @Test
    public void Find_format_by_mdprefix_not_successful() throws Exception {
        mvc.perform(get("/formats/oai_d")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot find format.")));
    }

    @Test
    public void Find_all_formats() throws Exception {
        mvc.perform(get("/formats")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void Mark_format_as_delete_successful() throws Exception {
        mvc.perform(delete("/formats/oai_dc")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void Mark_format_as_delete_not_successful_if_mdprefix_is_wrong() throws Exception {
        mvc.perform(delete("/formats/oai_d")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Undo_mark_format_as_delete_successful() throws Exception {
        mvc.perform(delete("/formats/oai_dc/undo")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void Undo_mark_format_as_delete_not_successful_if_undo_param_is_wrong() throws Exception {
        mvc.perform(delete("/formats/oai_dc/und")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMsg", is("The undo param is set, but wrong.")));
    }

    @Test
    public void Undo_mark_format_as_delete_not_successful_if_mdprefix_is_wrong() throws Exception {
        mvc.perform(delete("/formats/oai_d/undo")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMsg", is("Cannot undo delete format.")));
    }

}
