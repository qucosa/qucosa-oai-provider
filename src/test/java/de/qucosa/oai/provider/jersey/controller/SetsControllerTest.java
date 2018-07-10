/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.jersey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.application.config.SetConfigMapper;
import de.qucosa.oai.provider.controller.SetController;
import de.qucosa.oai.provider.data.objects.SetTestData;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDao;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SetsControllerTest extends JerseyTest {

    private ObjectMapper om = new ObjectMapper();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SetTestData.setspec = "ddc:850";
        SetTestData.setname = "Italian, Romanian, Rhaeto-Romanic literatures";
    }

    @Test
    public void Save_input_data_is_empty() {
        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(""));
        assertEquals(406, response.getStatus());
        assertEquals("Sets input data is empty.", response.readEntity(String.class));
    }

    @Test
    public void Save_successful_set_objects() throws Exception {
        Set<SetConfigMapper.Set> sets = new HashSet<>();
        sets.add(SetTestData.set());
        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(sets));
        Set<de.qucosa.oai.provider.persistence.pojos.Set> result = response.readEntity(Set.class);
        assertEquals(response.getStatus(), 200);
        assertEquals(1, result.size());
    }

    @Test
    public void Save_not_successful_set_object_if_setspec_is_null_or_empty() {
        SetConfigMapper.Set testSet = SetTestData.set();
        testSet.setSetSpec(null);
        Set<SetConfigMapper.Set> sets = new HashSet<>();
        sets.add(testSet);

        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(sets));
        assertEquals(406, response.getStatus());
        assertEquals("Set data object has null or empty values.", response.readEntity(String.class));
    }

    @Test
    public void Save_not_successful_set_object_if_setname_is_null_or_empty() {
        SetConfigMapper.Set testSet = SetTestData.set();
        testSet.setSetName(null);
        Set<SetConfigMapper.Set> sets = new HashSet<>();
        sets.add(testSet);

        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(sets));
        assertEquals(406, response.getStatus());
        assertEquals("Set data object has null or empty values.", response.readEntity(String.class));
    }

    @Test
    public void Response_if_input_is_an_empty_json_object() {
        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json("{}"));
        assertEquals(400, response.getStatus());
        assertEquals("Cannot build set objects.", response.readEntity(String.class));
    }

    @Test
    public void Update_set_object_successful() {
        Set<SetConfigMapper.Set> sets = new HashSet() {{
                add(SetTestData.set());
            }};
        Response response = target().path("sets/ddc:850").request().header("Content-Type", "application/json").put(Entity.json(sets));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void Update_set_if_setspec_param_unequal_to_object_setspec() {
        SetTestData.setspec = "blablub";
        Set<SetConfigMapper.Set> sets = new HashSet() {{
            add(SetTestData.set());
        }};
        Response response = target().path("sets/ddc:850").request().header("Content-Type", "application/json").put(Entity.json(sets));
        assertEquals(400, response.getStatus());
        assertEquals("Request param setspec and json data setspec are unequal.", response.readEntity(String.class));
    }

    @Test
    public void Update_set_if_setname_is_null_or_empty() {
        SetTestData.setname = null;
        Set<SetConfigMapper.Set> sets = new HashSet() {{
            add(SetTestData.set());
        }};
        Response response = target().path("sets/ddc:850").request().header("Content-Type", "application/json").put(Entity.json(sets));
        assertEquals(406, response.getStatus());
        assertEquals("Set data object has null or empty values.", response.readEntity(String.class));
    }

    @Test
    public void Delete_set_if_setspec_param_failed() {
        Response response = target().path("sets/").request().header("Content-Type", "application/json").delete();
        assertEquals(405, response.getStatus());
    }

    @Test
    public void Delete_set_if_setspec_param_does_not_exists() {
        Response response = target().path("sets/blablub").request().header("Content-Type", "application/json").delete();
        assertEquals(406, response.getStatus());
        assertEquals("Set with setspec blablub not found.", response.readEntity(String.class));
    }

    @Override
    protected Application configure() {
        SetController setsController = new SetController(new SetTestDao());

        ResourceConfig config = new ResourceConfig(SetController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(SetTestDao.class).to(PersistenceDao.class).in(RequestScoped.class);
                bind(setsController).to(SetController.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTermsDao(getClass().getResourceAsStream("/config/dissemination-config.json")));
        config.setProperties(props);
        return config;
    }

    private static class SetTestDao<T> extends PsqlRepository<T> {

        @Override
        public T create(T object) throws SQLException {
            Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();

            if (object instanceof Set) {

                for (de.qucosa.oai.provider.persistence.pojos.Set inputSet : (Set<de.qucosa.oai.provider.persistence.pojos.Set>) object) {

                    if (inputSet.getSetSpec() == null || inputSet.getSetSpec().isEmpty()) {
                        throw new SQLException("Set data object has null or empty values.");
                    }

                    if (inputSet.getSetName() == null || inputSet.getSetName().isEmpty()) {
                        throw new SQLException("Set data object has null or empty values.");
                    }

                    sets.add(inputSet);
                }
            }

            if (object instanceof de.qucosa.oai.provider.persistence.pojos.Set) {
                de.qucosa.oai.provider.persistence.pojos.Set inputSet = (de.qucosa.oai.provider.persistence.pojos.Set) object;

                if (inputSet.getSetSpec() == null || inputSet.getSetSpec().isEmpty()) {
                    throw new SQLException("Set data object has null or empty values.");
                }

                if (inputSet.getSetName() == null || inputSet.getSetName().isEmpty()) {
                    throw new SQLException("Set data object has null or empty values.");
                }

                sets.add(inputSet);
            }


            return (T) sets;
        }

        @Override
        public T update(T object) throws SQLException {
            Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();

            if (object instanceof Set) {

                for (de.qucosa.oai.provider.persistence.pojos.Set inputSet : (Set<de.qucosa.oai.provider.persistence.pojos.Set>) object) {

                    if (inputSet.getSetName() == null || inputSet.getSetName().isEmpty()) {
                        throw new SQLException("Set data object has null or empty values.");
                    }

                    sets.add(inputSet);
                }
            }

            return (T) sets;
        }

        @Override
        public void deleteByKeyValue(String key, T value) throws SQLException {

            if (!value.equals(SetTestData.set().getSetSpec())) {
                throw new SQLException("Set with setspec " + value + " not found.");
            }
        }
    }
}
