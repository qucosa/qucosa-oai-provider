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
import de.qucosa.oai.provider.dao.RecordTestDao;
import de.qucosa.oai.provider.dao.SetTestDao;
import de.qucosa.oai.provider.dao.SetsToRecordTestDao;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.RecordTransport;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
import org.junit.Before;
import org.junit.Test;
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
public class RecordControllerTest {
    private List<RecordTransport> transportList = null;

    private List<Record> records = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws IOException {
        transportList = om.readValue(TestData.RECORDS_INPUT, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
        records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class SetControllerTestConfiguration {
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

        @Bean
        public Dao recordDao() {
            return new RecordTestDao<Record>();
        }

        @Bean
        public RecordService recordService() {
            RecordService recordService = new RecordService();
            recordService.setDao(recordDao());
            return recordService;
        }

        @Bean
        public Dao setDao() {
            return new SetTestDao<Set>();
        }

        @Bean
        public SetService setService() {
            SetService setService = new SetService();
            setService.setDao(setDao());
            return setService;
        }

        @Bean
        public Dao disseminationDao() {
            return new DisseminationTestDao<Dissemination>();
        }

        @Bean
        public DisseminationService disseminationService() {
            DisseminationService disseminationService = new DisseminationService();
            disseminationService.setDao(disseminationDao());
            return disseminationService;
        }

        @Bean
        public Dao setsToRecordDao() {
            return new SetsToRecordTestDao();
        }
    }

    @Test
    public void Save_not_if_oaidc_dissemination_failed() throws Exception {

        for (RecordTransport rt : transportList) {

            if (rt.getFormat().getMdprefix().equals("oai_dc")) {
                rt.getFormat().setMdprefix("oi_dc");
            }
        }

        mvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(transportList)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Update_record_object() throws Exception {
        mvc.perform(put("/records/oai:example:org:qucosa:55887")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(records.get(0))))
                .andExpect(status().isOk());
    }

    @Test
    public void Update_record_object_not_successful_if_uid_is_wrong() throws Exception {
        mvc.perform(put("/records/oai:example:org:qucosa:5887")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(records.get(0))))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Unequal uid parameter with record object uid.")));
    }

    @Test
    public void Mark_record_as_deleted() throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/records/oai:example:org:qucosa:55887/true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        int deleted = Integer.valueOf(mvcResult.getResponse().getContentAsString());
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Mark_record_as_not_deleted() throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/records/oai:example:org:qucosa:55887/false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        int deleted = Integer.valueOf(mvcResult.getResponse().getContentAsString());
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Find_record_by_uid() throws Exception {
        mvc.perform(get("/records/oai:example:org:qucosa:55887")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("oai:example:org:qucosa:55887")))
                .andExpect(jsonPath("$.pid", is("qucosa:55887")));
    }
}
