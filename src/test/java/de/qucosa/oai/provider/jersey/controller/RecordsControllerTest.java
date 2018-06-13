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
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RecordsControllerTest extends JerseyTest {

    @Test
    public void Save_or_update_record_object_successfulö() throws ParseException, Exception {
        Response response = target().path("records").request().header("Content-Type", "application/json").post(Entity.json(record()));
        assertEquals(200, response.getStatus());
    }
    
    @Override
    protected Application configure() {
        PersistenceDaoInterface psqRepoDao = mock(RecordTestDao.class);
        RecordController recordController = new RecordController(psqRepoDao);

        ResourceConfig config = new ResourceConfig(RecordController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(RecordTestDao.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms(getClass().getResourceAsStream("/config/dissemination-config.json")));
        config.setProperties(props);
        return config;
    }
    
    private Record record() throws ParseException {
        Record record = new Record();
        record.setPid("qucosa:48672");
        record.setUid("example:oai:qucosa:48672");
        return record;
    }

    private static class RecordTestDao extends PsqlRepository {
        @Override
        public <T> T update(T object) throws SQLException {
            return (T) super.update(object);
        }
    }
}
