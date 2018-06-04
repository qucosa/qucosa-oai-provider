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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.process.internal.RequestScoped;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.postgres.FormatService;

@Path("/formats")
@RequestScoped
public class FormatsController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private FormatService formatService;
    
    @PostConstruct
    public void init() {
        formatService.setConnection(connection);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) throws SQLException, IOException {

        if (input == null && input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is empty or failed!").build();
        }

        Format format = buildSqlFormat(input);

        if (format == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formats json mapper object is failed!").build();
        }

        formatService.update(format);

        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("mdprefix") String mdprefix, String input) throws IOException, SQLException {

        if (mdprefix.isEmpty() || mdprefix == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        Format format = buildSqlFormat(input);

        if (format == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Format mapper is null!").build();
        }

        formatService.update(format);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("mdprefix") String mdprefix) throws SQLException {

        if (mdprefix.isEmpty() || mdprefix == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        formatService.deleteByKeyValue("mdprefix", mdprefix);

        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("{mdprefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response format(@Context ServletContext servletContext, @PathParam("mdprefix") String mdprefix) {

        if (mdprefix.isEmpty() || mdprefix == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mdprefix parameter is failed or empty!").build();
        }

        Format format = formatService.findByValue("mdprefix", mdprefix);

        if (format.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Format json mapper object is failed!").build();
        }

        return Response.status(200).entity(format).build();
    }
    
    private Format buildSqlFormat(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Format format = om.readValue(input, Format.class);
        return format;
    }
}
