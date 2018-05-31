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
    public Response save(String input) throws SQLException {
        Set<Format> formats = null;

        if (input != null && !input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is empty!").build();
        }

        formats = buildSqlSets(input);

        if (formats.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Formats build is failed!").build();
        }

        saveFormats(formats);

        return Response.status(Response.Status.OK).build();
    }

    @PUT
    @Path("{mdprefix}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("mdprefix") String mdprefix, String input) throws IOException, SQLException {

        if (mdprefix.isEmpty() || mdprefix == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The name parameter is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Format format = om.readValue(input, Format.class);

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
            return Response.status(Response.Status.BAD_REQUEST).entity("The name parameter is failed or empty!").build();
        }

        formatService.deleteByKeyValue("mdprefix", mdprefix);

        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("{mdprefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response format(@Context ServletContext servletContext, @PathParam("mdprefix") String mdprefix) {
        return Response.status(200).entity(true).build();
    }
    
    private Set<Format> buildSqlSets(String input) {
        Set<Format> formats = new HashSet<>();
        ObjectMapper om = new ObjectMapper();
        
        try {
            formats = om.readValue(input, om.getTypeFactory().constructCollectionType(Set.class, Format.class));
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return formats;
    }
    
    public void saveFormats(Set<Format> formats) throws SQLException {
        formatService.update(formats);
    }
}
