package de.qucosa.oai.provider.jersey.tests;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Application;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.ApplicationBinder;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissFormat;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissTermsDao;
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.persistence.pojos.Format;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FormatsController.class)
public class FormatsControllerTests extends JerseyTestAbstract {
    @Inject
    private FormatsController formatsController;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Binder binder = new AbstractBinder() {
            
            @Override
            protected void configure() {
                bindAsContract(FormatsController.class);
            }
        };
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(binder, new ApplicationBinder());
        locator.inject(this);
    }
    
    @Test
    public void updateFormats_Test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(formats());
        FormatsController fc = PowerMockito.spy(formatsController);
        PowerMockito.when(fc, MemberMatcher.method(FormatsController.class, "buildSqlSets", String.class))
            .withArguments(json)
            .thenCallRealMethod();
        PowerMockito.doNothing().when(fc, MemberMatcher.method(FormatsController.class, "saveFormats", Set.class))
            .withArguments(formats());
        fc.updateFormats(om.writeValueAsString(json));
    }
    
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(FormatsController.class);
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }
    
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return super.getTestContainerFactory();
    }
    
    private Set<Format> formats() {
        DissTerms dissTerms = (DissTerms) configure().getProperties().get("dissConf");
        Set<DissFormat> dissFormats = dissTerms.formats();
        Set<Format> formats = new HashSet<>();
        
        for(DissFormat df : dissFormats) {
            Format fm = new Format();
            fm.setMdprefix(df.getFormat());
            fm.setDissType(df.getDissType());
            fm.setLastpolldate(new Timestamp(new Date().getTime()));
            formats.add(fm);
        }
        
        return formats;
    }
}
