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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
    
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateFormats(String input) {
        
        if (input != null && !input.isEmpty()) {
            Set<Format> formats = buildSqlSets(input);
            
            if (!formats.isEmpty()) {
                saveFormats(formats);
            }
        }
    }
    
    @Path("/listFormats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFormats(@Context ServletContext servletContext) {
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
    
    public void saveFormats(Set<Format> formats) {
        formatService.update(formats);
    }
}
