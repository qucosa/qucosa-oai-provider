package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.dao.DisseminationTestDao;
import de.qucosa.oai.provider.dao.FormatTestDao;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import de.qucosa.oai.provider.persitence.model.Format;
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
        public FormatApi formatApi() {
            FormatApi formatApi = new FormatApi();
            formatApi.setDao(formatDao());
            return formatApi;
        }

        @Bean
        public Dao disseminationDao() {
            return new DisseminationTestDao();
        }

        @Bean
        public DisseminationApi disseminationApi() {
            DisseminationApi disseminationApi = new DisseminationApi();
            disseminationApi.setDao(disseminationDao());
            return disseminationApi;
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
    public void Find_disseminations_by_uid() throws Exception {
        mvc.perform(get("/dissemination/oai:example:org:qucosa:55887")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Mark_disseminations_as_deleted() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadiss/true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(true)));
    }

    @Test
    public void Mark_disseminations_as_not_deleted() throws Exception {
        mvc.perform(delete("/dissemination/oai:example:org:qucosa:55887/xmetadiss/false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(false)));
    }
}