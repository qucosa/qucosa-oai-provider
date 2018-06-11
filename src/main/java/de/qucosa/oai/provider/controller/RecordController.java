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
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/records")
@RequestScoped
public class RecordController {
    private PersistenceDaoInterface recordDao;
    
    private DissTerms terms;

    @Inject
    public RecordController(PersistenceDaoInterface recordDao) {
        this.recordDao = recordDao;
    }
    
    @GET
    @Path("{uid}")
    public Response find(@PathParam("uid") String uid) throws SQLException {

        if (uid == null || uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The uid paramter is failed or empty!").build();
        }

        Record record = recordDao.findByValue("uid", uid);

        if (record.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mapping object is failed!").build();
        }

        return Response.status(Response.Status.OK).entity(record).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) throws SQLException, IOException, SAXException {

        Record record = buildSqlObject(input);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Data json mapper object is failed!").build();
        }

        int[] result = recordDao.update(record);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @PUT
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") String uid, String input) throws IOException, SQLException, SAXException {

        if (uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("UID parameter is failed or empty!").build();
        }

        if (input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Record record = om.readValue(input, Record.class);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Record json mapper is failed!").build();
        }

        if (!record.getPid().equals(uid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update UID parameter and record PID ar unequal!").build();
        }

        int[] result = recordDao.update(record);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("uid") String uid) throws SQLException {

        if (uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("UID parameter is failed or empty!").build();
        }

        recordDao.deleteByKeyValue("uid", uid);

        return Response.status(Response.Status.OK).build();
    }
    
    private Record buildSqlObject(String json) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, Record.class);
    }
}
