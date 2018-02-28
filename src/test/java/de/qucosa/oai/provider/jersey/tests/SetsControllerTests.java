package de.qucosa.oai.provider.jersey.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Application;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.ApplicationConfigListener.DissTermsDao;
import de.qucosa.oai.provider.controller.SetsController;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SetsController.class)
public class SetsControllerTests extends JerseyTestAbstract {
    @Inject
    private SetsController setsController;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
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
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTermsDao());
        config.setProperties(props);
        return config;
    }
    
    @Test
    public void updateSets_Test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        File setSpecs = new File("/home/dseelig/opt/oaiprovider/config/list-set-conf.json");
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        
        try {
            json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        SetsController sc = PowerMockito.spy(setsController);
        PowerMockito.when(sc, MemberMatcher.method(SetsController.class, "buildSqlSets", String.class))
            .withArguments(om.writeValueAsString(json))
            .thenCallRealMethod();
        PowerMockito.when(sc, MemberMatcher.method(SetsController.class, "saveSetSpecs", Set.class))
            .withArguments(json)
            .thenReturn(null);
        sc.updateSets(om.writeValueAsString(json));
    }
}
