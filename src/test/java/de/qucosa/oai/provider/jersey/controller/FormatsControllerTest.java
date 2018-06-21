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

import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.data.objects.FormatTestData;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FormatsControllerTest extends JerseyTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        FormatTestData.mdprefix = "oai_dc";
        FormatTestData.schemaurl = "http://www.openarchives.org/OAI/2.0/oai_dc/";
        FormatTestData.namespace = "oai_dc";
    }

    @Test
    public void Save_new_format_successful() throws Exception {
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(FormatTestData.format()));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void Save_format_not_successful_if_schemaurl_property_is_empty() {
        FormatTestData.schemaurl = "";
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(FormatTestData.format()));
        assertEquals(406, response.getStatus());
        assertEquals("Unauthorized empty values in format object.", response.readEntity(String.class));
    }

    @Test
    public void Save_format_not_successful_if_mdprefix_property_is_empty() {
        FormatTestData.mdprefix = "";
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(FormatTestData.format()));
        assertEquals(406, response.getStatus());
        assertEquals("Unauthorized empty values in format object.", response.readEntity(String.class));
    }

    @Test
    public void Save_format_not_successful_if_namespace_property_is_empty() {
        FormatTestData.namespace = "";
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(FormatTestData.format()));
        assertEquals(406, response.getStatus());
        assertEquals("Unauthorized empty values in format object.", response.readEntity(String.class));
    }

    @Test
    public void Update_format_is_not_successful_if_mdprefix_parameter_is_failed() {
        Response response = target().path("formats/").request().header("Content-Type", "application/json").put(Entity.json(FormatTestData.format()));
        assertEquals(405, response.getStatus());
    }

    @Test
    public void Update_format_is_not_successful_if_cannot_build_format_object() {
        Response response = target().path("formats/" + FormatTestData.format().getMdprefix()).request().header("Content-Type", "application/json").put(Entity.json(""));
        assertEquals(400, response.getStatus());
        assertEquals("Cannot build format object.", response.readEntity(String.class));
    }

    @Test
    public void Update_format_is_not_successful_if_cannot_build_format_object_is_null() {
        Response response = target().path("formats/" + FormatTestData.format().getMdprefix()).request().header("Content-Type", "application/json").put(Entity.json("{}"));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void Update_format_not_successful_if_input_mdprefix_unequal_param_mdprefix() {
        Response response = target().path("formats/blablub").request().header("Content-Type", "application/json").put(Entity.json(FormatTestData.format()));
        assertEquals(406, response.getStatus());
        assertEquals("Request param mdprefix and json data mdprefix are unequal.", response.readEntity(String.class));
    }

    @Test
    public void Update_format_not_successful_in_return_is_id_failed() {
        FormatTestData.id = null;
        Response response = target().path("formats/" + FormatTestData.format().getMdprefix()).request().header("Content-Type", "application/json").put(Entity.json(FormatTestData.format()));
        assertEquals(406, response.getStatus());
        assertEquals("Cannot save or update format.", response.readEntity(String.class));
    }

    @Test
    public void Delete_format_not_successful_if_mdprefix_param_is_failed() {
        Response response = target().path("formats/").request().header("Content-Type", "application/json").delete();
        assertEquals(405, response.getStatus());
    }

    @Test
    public void Delete_format_not_successful_if_mdprefix_not_exists() {
        Response response = target().path("formats/blablub").request().header("Content-Type", "application/json").delete();
        assertEquals(406, response.getStatus());
        assertEquals("Cannot format mark as deleted, no rows affected.", response.readEntity(String.class));
    }

    @Test
    public void Find_format_not_successful_if_mdprefix_pathparam_failed() {
        Response response = target().path("formats/").request().header("Content-Type", "application/json").get();
        assertEquals(405, response.getStatus());
    }

    @Test
    public void Find_format_not_successful_if_datarow_not_found() {
        Response response = target().path("formats/blablub").request().header("Content-Type", "application/json").get();
        assertEquals(406, response.getStatus());
        assertEquals("Cannot find format object.", response.readEntity(String.class));
    }

    @Override
    protected Application configure() {
        PersistenceDaoInterface psqRepoDao = mock(FormatTestDao.class);
        FormatsController formatsController = new FormatsController(psqRepoDao);

        ResourceConfig config = new ResourceConfig(FormatsController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(FormatTestDao.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms(getClass().getResourceAsStream("/config/dissemination-config.json")));
        config.setProperties(props);
        config.registerInstances(formatsController);
        return config;
    }

    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("formats").build();
    }

    public static class FormatTestDao extends PsqlRepository {
        @Override
        public <T> T update(T object) throws SQLException {
            Format format = (Format) object;

            if (format.getSchemaUrl() == null || format.getNamespace() == null || format.getMdprefix() == null) {
                throw new SQLException("Unauthorized empty values in format object.");
            }

            if(format.getMdprefix().isEmpty() || format.getNamespace().isEmpty() || format.getSchemaUrl().isEmpty()) {
                throw new SQLException("Unauthorized empty values in format object.");
            }

            if (format.getId() == null) {
                throw new SQLException("Cannot save or update format.");
            }

            return (T) format;
        }

        @Override
        public <T> T findByValue(String column, String value) throws SQLException {

            if (!FormatTestData.format().getMdprefix().equals(value)) {
                throw new SQLException("Cannot find format object.");
            }

            return (T) FormatTestData.format();
        }

        @Override
        public <T> void deleteByKeyValue(String key, T value) throws SQLException {

            if (!FormatTestData.format().getMdprefix().equals(value)) {
                throw new SQLException("Cannot format mark as deleted, no rows affected.");
            }
        }
    }
}
