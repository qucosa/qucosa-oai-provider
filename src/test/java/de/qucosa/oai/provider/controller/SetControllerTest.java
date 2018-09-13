package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.dao.SetTestDao;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Set;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
    }

    @TestPropertySource("classpath:application.properties")
    @TestConfiguration
    public static class SetControllerTestConfiguration {

        @Bean
        public Dao setDao() {
            return new SetTestDao<Set>();
        }

        @Bean
        public SetService setApi() {
            SetService setApi = new SetService();
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
    public void Find_no_set_by_setspec() throws Exception {
        mvc.perform(get("/sets/ddc:120")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statuscode", is("404")))
                .andExpect(jsonPath("$.errorMsg", is("Cannot found set.")))
                .andExpect(jsonPath("$.method", is("find")));
    }

    @Test
    public void Find_all_sets() throws Exception {
        mvc.perform(get("/sets")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Save_single_set_object_not_successful() throws Exception {
        Set set = sets.get(0);
        set.setIdentifier(1);

        mvc.perform(post("/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(set)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Cannot save set objects.")))
                .andExpect(jsonPath("$.method", is("save")));
    }

    @Test
    public void Save_single_set_object() throws Exception {
        Set set = sets.get(0);
        mvc.perform(post("/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(set)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setid", is(1)));
    }

    @Test
    public void Save_collection_of_sets() throws Exception {
        mvc.perform(post("/sets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(sets)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void Update_set() throws Exception {
        Set set = sets.get(0);
        set.setSetName("quatsch");
        mvc.perform(put("/sets/ddc:1200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(set)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setname", is("quatsch")));
    }

    @Test
    public void Update_set_not_successful() throws Exception {
        Set set = sets.get(0);
        set.setSetName("quatsch");
        mvc.perform(put("/sets/ddc:120")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(set)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Cannot update set.")))
                .andExpect(jsonPath("$.statuscode", is("406")));
    }

    @Test
    public void Mark_set_as_delete() throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/sets/ddc:1200/true")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        int deleted = Integer.valueOf(mvcResult.getResponse().getContentAsString());
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Mark_set_as_delete_not_successful() throws Exception {
        mvc.perform(delete("/sets/ddc:120/true")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.errorMsg", is("Cannot delete set.")));
    }

    @Test
    public void Mark_set_as_not_delete() throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/sets/ddc:1200/false")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        int deleted = Integer.valueOf(mvcResult.getResponse().getContentAsString());
        assertThat(1).isEqualTo(deleted);
    }
}
