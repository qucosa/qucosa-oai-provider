package de.qucosa.oai.provider.jersey.tests;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.controller.SetsController;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class JerseySetsTest extends JerseyTest {
    Connection connection = null;
    
    PersistenceServiceInterface service = new SetService();
    
    @Inject
    private SetsController setsController;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
        
        Binder binder = new AbstractBinder() {
            
            @Override
            protected void configure() {
                bindAsContract(SetsController.class);
            }
        };
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
        locator.inject(this);
    }
    
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(SetsController.class);
        return config;
    }
    
    @SuppressWarnings("unused")
    @Test
    public void saveSets_Test() {
        setsController.setTest(true);
        ObjectMapper om = new ObjectMapper();
        File setSpecs = new File("/home/dseelig/opt/oaiprovider/config/list-set-conf.json");
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        
        try {
            json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            Response response = setsController.addSets(om.writeValueAsString(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
