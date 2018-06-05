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

package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.postgres.DisseminationDao;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Path("/dissemination")
@RequestScoped
public class DisseminationController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();

    private PersistenceServiceInterface disseminationService;

    @Inject
    public DisseminationController (DisseminationDao disseminationService) {
        this.disseminationService = disseminationService;
    }

    @PostConstruct
    public void init() {
        disseminationService.setConnection(connection);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) throws IOException, SQLException, SAXException {

        if (input.isEmpty() || input == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The input data object is empty or failed!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = om.readValue(input, Dissemination.class);

        if (dissemination == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The dissemination json object mapping is failed!").build();
        }

        disseminationService.update(dissemination);

        return Response.status(200).entity(true).build();
    }
}
