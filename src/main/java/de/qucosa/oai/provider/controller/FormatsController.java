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
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("/formats")
@RequestScoped
public class FormatsController {

    private PersistenceDaoInterface formatDao;

    @Inject
    public FormatsController(PersistenceDaoInterface formatDao) {
        this.formatDao = formatDao;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) throws SQLException, IOException, SAXException {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is empty or failed!").build();
        }

        Format format = buildSqlFormat(input);

        if (format == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formats json mapper object is failed!").build();
        }

        Format result = formatDao.update(format);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @PUT
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("mdprefix") String mdprefix, String input) throws IOException, SQLException, SAXException {

        if (mdprefix.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        Format format = buildSqlFormat(input);

        if (format == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Format mapper is null!").build();
        }

        formatDao.update(format);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("mdprefix") String mdprefix) throws SQLException {

        if (mdprefix.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        formatDao.deleteByKeyValue("mdprefix", mdprefix);

        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("{mdprefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response format(@PathParam("mdprefix") String mdprefix) throws SQLException {

        if (mdprefix.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        Format format = formatDao.findByValue("mdprefix", mdprefix);

        if (format == null || format.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Format json mapper object is failed!").build();
        }

        return Response.status(200).entity(format).build();
    }
    
    private Format buildSqlFormat(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(input, Format.class);
    }
}
