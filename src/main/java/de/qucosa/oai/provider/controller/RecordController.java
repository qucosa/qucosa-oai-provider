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
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.postgres.RecordService;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Path("/records")
@RequestScoped
public class RecordController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private RecordService service;
    
    private DissTerms terms;
    
    @PostConstruct
    public void init() {
        service.setConnection(connection);
    }

    @GET
    @Path("{pid}")
    public Response find(@PathParam("pid") String pid) throws SQLException {

        if (pid == null || pid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The pid paramter is failed or empty!").build();
        }

        Record record = service.findByValue("pid", pid);

        if (record.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mapping object is failed!").build();
        }

        return Response.status(Response.Status.OK).entity(record).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) throws SQLException, IOException {

        if (input.isEmpty() && input == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The input data object is failed or empty!").build();
        }

        Record record = buildSqlObject(input);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Data json mapper object is failed!").build();
        }

        service.update(record);

        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("{pid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("pid") String pid, String input) throws IOException, SQLException {

        if (pid.isEmpty() || pid == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("PID parameter is failed or empty!").build();
        }

        if (input.isEmpty() || input == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Record record = om.readValue(input, Record.class);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Record json mapper is failed!").build();
        }

        if (!record.getPid().equals(pid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update PID parameter and record PID ar unequal!").build();
        }

        service.update(record);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{pid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("pid") String pid) throws SQLException {

        if (pid.isEmpty() || pid == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("PID parameter is failed or empty!").build();
        }

        service.deleteByKeyValue("pid", pid);

        return Response.status(Response.Status.OK).build();
    }
    
    private Record buildSqlObject(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Record record = om.readValue(json, Record.class);
        return record;
    }
}
