package de.qucosa.oai.provider.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

@Path("/sets")
public class SetsController extends ControllerAbstract {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private PersistenceServiceInterface setService;
    
    @PostConstruct
    public void init() {
        setService.setConnection(connection);
    }
    
    @SuppressWarnings("unused")
    @GET
    @Path("/ListSets")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSetsXml() throws IOException, SAXException {
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSetsByte(String input) {
        ObjectMapper om = new ObjectMapper();
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        
        try {
            json = om.readValue(input, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        saveSetSpecs(buildSqlSets(json));
        
        return Response.status(200).entity(input).build();
    }
    
    private Set<de.qucosa.oai.provider.persistence.pojos.Set> buildSqlSets(Set<de.qucosa.oai.provider.persistence.pojos.Set> json) {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        
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
