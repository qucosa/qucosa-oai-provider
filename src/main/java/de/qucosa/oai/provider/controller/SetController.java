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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.postgres.SetService;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

@Path("/sets")
@RequestScoped
public class SetController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private SetService setService;
    
    @PostConstruct
    public void init() {
        setService.setConnection(connection);
    }
    
    @SuppressWarnings("unused")
    @GET
    @Path("/ListSets")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSetsXml(@Context ServletContext servletContext) throws IOException, SAXException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = setService.findAll();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            Node listSets = document.createElement("ListSets");
            ObjectMapper objectMapper = new ObjectMapper();
            
            for (de.qucosa.oai.provider.persistence.pojos.Set set : sets) {
                Node setNode = document.importNode(DocumentXmlUtils.node(set.getDoc()), true);
                listSets.appendChild(setNode);
            }
            
            document.appendChild(listSets);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return Response.status(200).entity(DocumentXmlUtils.resultXml(document)).build();
    }
    
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateSets(String input) throws JsonParseException, JsonMappingException, IOException {
        
        if (input != null && !input.isEmpty()) {
            Set<de.qucosa.oai.provider.persistence.pojos.Set> saveRes = buildSqlSets(input);
            
            if (saveRes != null && !saveRes.isEmpty()) {
                saveSetSpecs(saveRes);
            }
        }
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
    
    private void saveSetSpecs(Set<de.qucosa.oai.provider.persistence.pojos.Set> sets) {
        setService.update(sets);
    }
}
