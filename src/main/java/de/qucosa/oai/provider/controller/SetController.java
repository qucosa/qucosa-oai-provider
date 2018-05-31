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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.postgres.SetService;

@Path("/sets")
@RequestScoped
public class SetController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    private final PersistenceServiceInterface setService;

    @Inject
    public SetController(SetService setService) {
        this.setService = setService;
    }
    
    @PostConstruct
    public void init() {
        setService.setConnection(connection);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(String input) throws JsonParseException, JsonMappingException, IOException, SQLException, SAXException {
        
        if (input != null && !input.isEmpty()) {
            Set<de.qucosa.oai.provider.persistence.pojos.Set> saveRes = buildSqlSets(input);
            
            if (saveRes != null && !saveRes.isEmpty()) {
                saveSetSpecs(saveRes);
            }
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @PUT
    @Path("{setspec}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("setspec") String setspec, String input) throws IOException, SQLException, SAXException {
        ObjectMapper om = new ObjectMapper();
        de.qucosa.oai.provider.persistence.pojos.Set set = om.readValue(input, de.qucosa.oai.provider.persistence.pojos.Set.class);

        if (!set.getSetSpec().equals(setspec)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Request param setspec and json data setspec are unequal.").build();
        }

        set.setDocument(setSpecXml(set));

        saveSetSpecs(set);

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @DELETE
    @Path("{setspec}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("setspec") String setspec) throws SQLException {

        if (setspec.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The setspec param is failed or empty!").build();
        }

        setService.deleteByKeyValue("setspec", setspec);

        return Response.status(Response.Status.OK).build();
    }
    
    private Set<de.qucosa.oai.provider.persistence.pojos.Set> buildSqlSets(String input) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper();
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = om.readValue(input, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        
        for (de.qucosa.oai.provider.persistence.pojos.Set set : json) {
            de.qucosa.oai.provider.persistence.pojos.Set data = new de.qucosa.oai.provider.persistence.pojos.Set();
            data.setSetSpec(set.getSetSpec());
            data.setSetName(set.getSetName());
            data.setPredicate(set.getPredicate());
            data.setDocument(setSpecXml(set));
            sets.add(data);
        }
        
        return sets;
    }

    private Document setSpecXml(de.qucosa.oai.provider.persistence.pojos.Set set) {
        Document document = null;
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.newDocument();
            Element root = document.createElement("set");
            
            Element setSpec = document.createElement("setSpec");
            setSpec.appendChild(document.createTextNode(set.getSetSpec()));
            root.appendChild(setSpec);
            
            Element setName = document.createElement("setName");
            setName.appendChild(document.createTextNode(set.getSetName()));
            root.appendChild(setName);
            
            document.appendChild(root);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println(document.getDocumentElement().getTextContent());
        return document;
    }

    private <T> void saveSetSpecs(T object) throws SQLException, IOException, SAXException {
        setService.update(object);
    }
}
