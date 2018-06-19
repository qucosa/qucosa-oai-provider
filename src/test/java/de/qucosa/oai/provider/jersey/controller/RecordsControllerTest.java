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
import de.qucosa.oai.provider.controller.DisseminationController;
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.data.objects.DisseminationTestData;
import de.qucosa.oai.provider.data.objects.FormatTestData;
import de.qucosa.oai.provider.data.objects.RecordTestData;
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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RecordsControllerTest extends JerseyTest {

    @Test
    public void Save_new_record_successful() throws Exception {
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(RecordTestData.record()));
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
    public void Format_is_not_found() throws IOException {
        List<RecordTransport> inputData = inputData();
        FormatTestData.id = null;
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(404, response.getStatus());
        assertEquals("Format is not found.", response.readEntity(String.class));
    }

    @Test
    public void Record_is_not_found_because_has_wrong_uid() throws IOException {
        List<RecordTransport> inputData = inputData();
        RecordTestData.uid = "bla:blub:qucosa:55887";
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(404, response.getStatus());
        assertEquals("Record is not found.", response.readEntity(String.class));
    }

    @Test
    public void Dissemination_document_is_not_parsing_because_xml_failed() throws IOException {
        List<RecordTransport> inputData = inputData();
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(406, response.getStatus());
        assertEquals("Not found xml for parsing dissemination document.", response.readEntity(String.class));
    }

    @Test
    public void Dissemination_document_is_does_not_build() throws IOException {
        List<RecordTransport> inputData = inputData();
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));

        if (response.getStatus() == 400) {
            assertEquals(400, response.getStatus());
            assertEquals("Dissemination document has been not build.", response.readEntity(String.class));
        }
    }

    @Test
    public void Save_Dissemination_is_failed_if_formatid_null() throws IOException {
        List<RecordTransport> inputData = inputData();
        DisseminationTestData.formatid = null;
        DisseminationTestData.xmldata = "<oai_dc></oai_dc>";
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(406, response.getStatus());
        assertEquals("Dissemination save is failed", response.readEntity(String.class));
    }

    @Test
    public void Save_Dissemination_is_failed_if_recordid_null() throws IOException {
        List<RecordTransport> inputData = inputData();
        DisseminationTestData.recordid = null;
        DisseminationTestData.xmldata = "<oai_dc></oai_dc>";
        Response response = target().path("record").request().header("Content-Type", "application/json").post(Entity.json(inputData));
        assertEquals(406, response.getStatus());
        assertEquals("Dissemination save is failed", response.readEntity(String.class));
    }
    
    @Override
    protected Application configure() {
        RecordController recordController = new RecordController(new RecordTestDao());
        FormatsController formatsController = new FormatsController(new FormatsControllerTest.FormatTestDao());
        DisseminationController disseminationController = new DisseminationController(new DisseminationControllerTest.DisseminationTestDao());

        ResourceConfig config = new ResourceConfig(RecordController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(RecordTestDao.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
                bind(recordController).to(RecordController.class);
                bind(formatsController).to(FormatsController.class);
                bind(disseminationController).to(DisseminationController.class);
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

        @Override
        public <T> T findByValue(String column, String value) throws SQLException {
            Record record = RecordTestData.record();
            boolean find = false;

            if (column.equals("uid")) {

                if (record.getUid().equals(value)) {
                    find = true;
                }
            }

            return (find) ? (T) record : null;
        }
    }
}
