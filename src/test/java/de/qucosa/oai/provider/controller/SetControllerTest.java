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
import de.qucosa.oai.provider.persistence.model.Set;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class SetControllerTest {
    private List<Set> sets = null;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TestRestTemplate restTemplate;

//    @TestPropertySource("classpath:application.properties")
//    @TestConfiguration
//    public static class SetControllerTestConfiguration {
//
//        @Bean
//        public Dao setDao() {
//            return new SetTestDao<Set>();
//        }
//
//        @Bean
//        public SetService setService() {
//            SetService setService = new SetService();
//            setService.setDao(setDao());
//            return setService;
//        }
//    }

//    @Test
//    public void Find_set_by_setspec() throws Exception {
//        mvc.perform(get("/sets/ddc:1200")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.setspec", is("ddc:1200")));
//    }

//    @Test
//    public void Find_no_set_by_setspec() throws Exception {
//        mvc.perform(get("/sets/ddc:120")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.statuscode", is("404")))
//                .andExpect(jsonPath("$.errorMsg", is("Cannot found set.")))
//                .andExpect(jsonPath("$.method", is("find")));
//    }
//
//    @Test
//    public void Find_all_sets() throws Exception {
//        mvc.perform(get("/sets")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    public void Save_single_set_object_not_successful() throws Exception {
//        Set set = sets.get(0);
//        set.setIdentifier(1);
//
//        mvc.perform(post("/sets")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(set)))
//                .andExpect(status().isNotAcceptable())
//                .andExpect(jsonPath("$.errorMsg", is("Cannot save set objects.")))
//                .andExpect(jsonPath("$.method", is("save")));
//    }
//
//    @Test
//    public void Save_single_set_object() throws Exception {
//        Set set = sets.get(0);
//        mvc.perform(post("/sets")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(set)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.setid", is(1)));
//    }
//
//    @Test
//    public void Save_collection_of_sets() throws Exception {
//        mvc.perform(post("/sets")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(sets)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//    }
//
//    @Test
//    public void Update_set() throws Exception {
//        Set set = sets.get(0);
//        set.setSetName("quatsch");
//        mvc.perform(put("/sets/ddc:1200")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(set)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.setname", is("quatsch")));
//    }
//
//    @Test
//    public void Update_set_not_successful() throws Exception {
//        Set set = sets.get(0);
//        set.setSetName("quatsch");
//        mvc.perform(put("/sets/ddc:120")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(om.writeValueAsString(set)))
//                .andExpect(status().isNotAcceptable())
//                .andExpect(jsonPath("$.errorMsg", is("Cannot update set.")))
//                .andExpect(jsonPath("$.statuscode", is("406")));
//    }
//
//    @Test
//    public void Mark_set_as_delete_successful() throws Exception {
//        mvc.perform(delete("/sets/ddc:1200")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void Mark_set_as_delete_not_successful_if_setspec_is_wrong() throws Exception {
//        mvc.perform(delete("/sets/ddc:120")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void Undo_mark_set_as_delete_successful() throws Exception {
//        mvc.perform(delete("/sets/ddc:1200/undo")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void Undo_mark_set_as_delete_not_successful_if_undo_param_is_wrong() throws Exception {
//        mvc.perform(delete("/sets/ddc:1200/und")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errorMsg", is("The undo param is set, but wrong.")));
//    }
//
//    @Test
//    public void Undo_mark_set_as_delete_not_successful_if_setspec_is_wrong() throws Exception {
//        mvc.perform(delete("/sets/ddc:120/undo")
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errorMsg", is("Cannot undo delete set.")));
//    }
}
