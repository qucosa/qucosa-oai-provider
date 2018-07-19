package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.dao.RecordTestDao;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RecordControllerTest {
    private List<RecordTransport> transportList = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws IOException {
        transportList = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class SetControllerTestConfiguration {

        @Bean
        public <T> Dao<T> recordDao() {
            return new RecordTestDao<>();
        }

        @Bean
        public RecordApi recordApi() {
            RecordApi recordApi = new RecordApi();
            recordApi.setDao(recordDao());
            return recordApi;
        }
    }

    @Test
    public void Save_record_transport_data() throws Exception {
        mvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.RECORDS))
                .andExpect(status().isOk());
    }
}
