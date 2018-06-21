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
import static org.mockito.Mockito.mock;

public class FormatsControllerTest extends JerseyTest {

    @Test
    public void Create_or_update_format_object_successful() throws Exception {
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(FormatTestData.format()));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void Create_or_update_not_successful_format_object_if_has_object_empty_schemaurl() {
        Format format = FormatTestData.format();
        format.setSchemaUrl("");
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(format));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void Create_or_update_not_successful_format_object_if_has_object_empty_mdprefix() {
        Format format = FormatTestData.format();
        format.setMdprefix("");
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(format));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void Create_or_update_not_successful_format_object_if_has_object_empty_namespace() {
        Format format = FormatTestData.format();
        format.setNamespace("");
        Response response = target().path("formats").request().header("Content-Type", "application/json").post(Entity.json(format));
        assertEquals(500, response.getStatus());
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
                throw new SQLException("Unauthorized null values in format object.");
            }

            if(format.getMdprefix().isEmpty() || format.getNamespace().isEmpty() || format.getSchemaUrl().isEmpty()) {
                throw new SQLException("Unauthorized empty values in format object.");
            }

            return super.update(object);
        }

        @Override
        public <T> T findByValue(String column, String value) throws SQLException {
            return (T) FormatTestData.format();
        }
    }
}
