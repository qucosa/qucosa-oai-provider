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
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.utils.DateTimeConverter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordsControllerTest extends JerseyTest {
    private RecordController recordController;

    private PersistenceDaoInterface psqRepoDao;
    
    @Test
    public void Save_new_record() throws ParseException, Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(record());
        when(psqRepoDao.update(json)).thenReturn(new int[0]);
        Response response = target().path("records").request().header("Content-Type", "application/json").post(Entity.json(record()));
        assertEquals(response.getStatus(), 200);
    }
    
    @Override
    protected Application configure() {
        psqRepoDao = mock(PsqlRepository.class);
        recordController = new RecordController((PsqlRepository) psqRepoDao);

        ResourceConfig config = new ResourceConfig(RecordController.class);
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
    
    private Record record() throws ParseException {
        Record record = new Record();
        record.setPid("qucosa:48672");
        record.setDatestamp(DateTimeConverter.timestampWithTimezone("2017-12-14T09:42:45Z"));
        return record;
    }
}