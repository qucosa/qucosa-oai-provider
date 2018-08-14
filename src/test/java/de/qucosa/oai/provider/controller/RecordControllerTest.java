package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.dao.DisseminationTestDao;
import de.qucosa.oai.provider.dao.FormatTestDao;
import de.qucosa.oai.provider.dao.RecordTestDao;
import de.qucosa.oai.provider.dao.SetTestDao;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Record;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
import de.qucosa.oai.provider.persitence.model.Set;
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
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        public FormatApi formatApi() {
            FormatApi formatApi = new FormatApi();
            formatApi.setDao(formatDao());
            return formatApi;
        }

        @Bean
        public Dao recordDao() {
            return new RecordTestDao<Record>();
        }

        @Bean
        public RecordApi recordApi() {
            RecordApi recordApi = new RecordApi();
            recordApi.setDao(recordDao());
            return recordApi;
        }

        @Bean
        public Dao setDao() {
            return new SetTestDao<Set>();
        }

        @Bean
        public SetApi setApi() {
            SetApi setApi = new SetApi();
            setApi.setDao(setDao());
            return setApi;
        }

        @Bean
        public Dao disseminationDao() {
            return new DisseminationTestDao<Dissemination>();
        }

        @Bean
        public DisseminationApi disseminationApi() {
            DisseminationApi disseminationApi = new DisseminationApi();
            disseminationApi.setDao(disseminationDao());
            return disseminationApi;
        }
    }

    @Test
    public void Save_record_transport_data() throws Exception {
        mvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.RECORDS_INPUT))
                .andExpect(status().isOk());
    }

    @Test
    public void Update_record_object() throws Exception {
        mvc.perform(put("/records/oai:example:org:qucosa:55887")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(records.get(0))))
                .andExpect(status().isOk());
    }

    @Test
    public void Mark_record_as_deleted() throws Exception {
        mvc.perform(delete("/records/oai:example:org:qucosa:55887/true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(true)));
    }

    @Test
    public void Mark_record_as_not_deleted() throws Exception {
        mvc.perform(delete("/records/oai:example:org:qucosa:55887/false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(false)));
    }
}
