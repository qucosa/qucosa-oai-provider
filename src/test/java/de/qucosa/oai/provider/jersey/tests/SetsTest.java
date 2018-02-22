package de.qucosa.oai.provider.jersey.tests;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.controller.SetsController;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class SetsTest extends JerseyTest {
    Connection connection = null;
    
    PersistenceServiceInterface service = new SetService();
    
    @SuppressWarnings("unused")
    private SetsController setsController = null;
    
    @Before
    public void init() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
        setsController = new SetsController();
    }
    
    @Override
    protected Application configure() {
        return new ResourceConfig(SetsController.class);
    }
    
    @SuppressWarnings("unused")
    @Test
    public void postSets_Test() {
        ObjectMapper om = new ObjectMapper();
        File setSpecs = new File("/home/dseelig/opt/oaiprovider/config/list-set-conf.json");
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        
        try {
            json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Response response = target("sets/add").request().post(Entity.json(json));
    }
}
