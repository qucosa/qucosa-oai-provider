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
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.helper.RestControllerContainerFactory;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordsControllerTest extends JerseyTest {

    private RecordController recordController;

    private FormatsController formatsController;

    @Test
    public void Save_new_record_successful() throws Exception {
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(record()));
        assertEquals(200, response.getStatus());
    }

    @Test
    public void Check_if_oaidc_format_is_not_exists() throws IOException {
        List<RecordTransport> inputData = inputData();

        for (RecordTransport rt : inputData) {

            if (rt.getMdprefix().equals("oai_dc")) {
                rt.setMdprefix("");
                break;
            }
        }

        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        response.readEntity(String.class);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void Check_if_format_return_is_null() throws SQLException, IOException, SAXException {
        List<RecordTransport> inputData = inputData();
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void bla() throws IOException {
        List<RecordTransport> inputData = inputData();

//        for (RecordTransport rt : inputData) {
//
//            if (rt.getMdprefix().equals("oai_dc")) {
//                Format format = new Format();
//                RecordController rc = mock(RecordController.class);
//                when(rc.format(any(Format.class))).thenReturn(format);
//                break;
//            }
//        }

        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData()));
        System.out.println(response.readEntity(String.class));
    }
    
    @Override
    protected Application configure() {
        PersistenceDaoInterface psqRepoDao = mock(RecordTestDao.class);
        recordController = new RecordController(psqRepoDao);
        formatsController = new FormatsController(new FormatsControllerTest.FormatTestDao());

        ResourceConfig config = new ResourceConfig(RecordController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(RecordTestDao.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
                bind(recordController).to(RecordController.class);
                bind(formatsController).to(FormatsController.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms(getClass().getResourceAsStream("/config/dissemination-config.json")));
        config.setProperties(props);
        return config;
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new RestControllerContainerFactory();
    }
    
    private Record record() throws ParseException {
        Record record = new Record();
        record.setPid("qucosa:48672");
        record.setUid("example:oai:qucosa:48672");
        return record;
    }

    private List<RecordTransport> inputData() throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(getClass().getResourceAsStream("/data/record-transport.json"),
                om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
    }

    private static class RecordTestDao extends PsqlRepository {
        @Override
        public <T> T update(T object) throws SQLException {
            return (T) super.update(object);
        }
    }
}
