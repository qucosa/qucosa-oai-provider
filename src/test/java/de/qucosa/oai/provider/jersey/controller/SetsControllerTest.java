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
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.application.mapper.SetsConfig;
import de.qucosa.oai.provider.controller.SetController;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetsControllerTest extends JerseyTest {

    private PersistenceDaoInterface psqlDao;

    private ObjectMapper om = new ObjectMapper();

    @Test
    public void Save_successful_set_objects() throws Exception {
        java.util.Set sets = new HashSet();
        sets.add(set());
        when(psqlDao.update(om.writeValueAsString(sets))).thenReturn(new int[0]);
        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(sets));
        assertEquals(response.getStatus(), 200);
    }

    @Test
    public void Retrun_bad_request_response_if_input_is_empty_json_object() {
        Response response = target().path("sets").request().header("Content-Type", "application/json").post(Entity.json(""));
        response.readEntity(String.class);
        assertEquals(response.getStatus(), 400);
    }

    @Override
    protected Application configure() {
        psqlDao = mock(PsqlRepository.class);
        SetController setsController = new SetController(psqlDao);

        ResourceConfig config = new ResourceConfig(SetController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(PsqlRepository.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }

    private SetsConfig.Set set() {
        SetsConfig.Set setCnf = new SetsConfig.Set();
        setCnf.setSetSpec("ddc:850");
        setCnf.setSetName("Italian, Romanian, Rhaeto-Romanic literatures");
        setCnf.setPredicate("xDDC=850");

        return setCnf;
    }
}
