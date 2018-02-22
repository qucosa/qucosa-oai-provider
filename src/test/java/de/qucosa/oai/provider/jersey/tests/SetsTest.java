package de.qucosa.oai.provider.jersey.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.controller.SetsController;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class SetsTest extends JerseyTest {
    Connection connection = null;
    
    PersistenceServiceInterface service = new SetService();
    
    @Before
    public void connect() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Override
    protected Application configure() {
        return new ResourceConfig(SetsController.class);
    }
    
    @Test
    public void postSets_Test() {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = service.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(sets);
            out.flush();
            baos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        Response response = target("sets/add").request().post(Entity.entity(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM));
    }
}
