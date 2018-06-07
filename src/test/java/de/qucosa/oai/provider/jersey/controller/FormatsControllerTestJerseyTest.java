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
import de.qucosa.oai.provider.application.ApplicationBinder;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.postgres.FormatDao;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FormatsControllerTestJerseyTest extends JerseyTest {
    private PersistenceDaoInterface formatDao;

    private FormatsController formatsController;

    @Test
    public void updateFormats_Test() throws Exception {
        int[] ex = new int[0];
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(format());
        when(formatDao.update(format())).thenReturn(ex);
        Response response = target().path("formats").request().post(Entity.json(format()));
        assertEquals(response.getStatus(), 200);
    }

    @Override
    protected Application configure() {
        formatDao = mock(FormatDao.class);
        formatsController = new FormatsController((FormatDao) formatDao);

        ResourceConfig config = new ResourceConfig(FormatsController.class);
        config.register(new ApplicationBinder());
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        config.registerInstances(formatsController);
        return config;
    }

    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("formats").build();
    }

    private Format format() {
        Format fm = new Format();
        fm.setMdprefix("xmetadiss");
        fm.setLastpolldate(new Timestamp(new Date().getTime()));
        return fm;
    }
}
