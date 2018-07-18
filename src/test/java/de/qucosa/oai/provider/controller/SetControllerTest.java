package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.dao.SetTestDao;
import de.qucosa.oai.provider.persitence.Dao;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SetControllerTest {
    private List<Set> sets = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() throws IOException {
//        ObjectMapper om = new ObjectMapper();
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class SetControllerTestConfiguration {

        @Bean
        public <T> Dao<T> setDao() {
            return new SetTestDao<>();
        }

        @Bean
        public SetApi setApi() {
            SetApi setApi = new SetApi();
            setApi.setDao(setDao());
            return setApi;
        }
    }

    @Test
    public void Find_set_by_setspec() throws Exception {
        mvc.perform(get("/sets/ddc:1200")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setspec", is("ddc:1200")));
    }

    @Test
    public void Find_all_sets() throws Exception {
        mvc.perform(get("/sets")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Save_single_set_object() throws Exception {
        Set set = sets.get(0);

        mvc.perform(post("/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(set)))
//                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.setid", is(1)));
    }
}
