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
    public Response save(String input) {
        Format format = null;

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is empty or failed!").build();
        }

        try {
            format = buildSqlFormat(input);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build format object.").build();
        }

        if (format == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formats json mapper object is failed!").build();
        }

        try {
            format = formatDao.update(format);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(format).build();
    }

    @PUT
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("mdprefix") String mdprefix, String input) {
        Format format = null;

        try {
            format = buildSqlFormat(input);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build format object.").build();
        }

        if (!format.getMdprefix().equals(mdprefix)) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Request param mdprefix and json data mdprefix are unequal.").build();
        }

        try {
            format = formatDao.update(format);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(format).build();
    }

    @DELETE
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("mdprefix") String mdprefix) {

        try {
            formatDao.deleteByKeyValue("mdprefix", mdprefix);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("{mdprefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response format(@PathParam("mdprefix") String mdprefix) {
        Format format = null;

        try {
            format = formatDao.findByValue("mdprefix", mdprefix);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(200).entity(format).build();
    }
    
    private Format buildSqlFormat(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(input, Format.class);
    }
}
