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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.postgres.RecordService;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

@Path("/records")
@RequestScoped
public class RecordController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private RecordService service;
    
    private DissTerms terms = null;
    
    @PostConstruct
    public void init() {
        service.setConnection(connection);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response listIdentifieres(@Context ServletContext servletContext) throws IOException, SAXException {
        terms = (DissTerms) servletContext.getAttribute("dissConf");
        terms.getMapXmlNamespaces();
        Set<Record> identifiers = service.findAll();
        Document document = identifieres(identifiers);
        return Response.status(200).entity(DocumentXmlUtils.resultXml(document)).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateIdentifieres(String input) {

        if (!input.isEmpty() && input != null) {
            Set<Record> identifiers = buildSqlObjects(input);
            
            if (!identifiers.isEmpty()) {
                saveIdentifieres(identifiers);
            }
        }
    }
    
    private Set<Record> buildSqlObjects(String json) {
        ObjectMapper om = new ObjectMapper();
        Set<Record> identifiers = new HashSet<>();
        
        try {
            identifiers = om.readValue(json, om.getTypeFactory().constructCollectionType(Set.class, Record.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return identifiers;
    }
    
    private void saveIdentifieres(Set<Record> data) {
        service.update(data);
    }
    
    private Document identifieres(Set<Record> identifiers) {
        Document document = DocumentXmlUtils.document(null, true);
        return document;
    }
}
