package de.qucosa.oai.provider.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.SetService;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

@Path("/sets")
public class SetsController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();

    @SuppressWarnings("unused")
    @GET
    @Path("/ListSets")
    @Produces(MediaType.APPLICATION_XML)
    public Response getSetsXml() throws IOException, SAXException {
        PersistenceServiceInterface service = service();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = service.findAll();
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

    private PersistenceServiceInterface service() {
        PersistenceServiceInterface service = new SetService();
        service.setConnection(connection);
        return service;
    }
}
