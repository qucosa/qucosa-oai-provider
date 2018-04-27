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

package de.qucosa.oai.provider.jersey.tests;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.ApplicationBinder;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.IdentifieresController;
import de.qucosa.oai.provider.persistence.pojos.Identifier;
import de.qucosa.oai.provider.persistence.utils.DateTimeConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IdentifieresController.class)
public class IdentifieresControllerTests extends JerseyTestAbstract {
    @Inject
    private IdentifieresController controller;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Binder binder = new AbstractBinder() {
            
            @Override
            protected void configure() {
                bindAsContract(IdentifieresController.class);
            }
        };
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(binder, new ApplicationBinder());
        locator.inject(this);
    }
    
    @SuppressWarnings("unused")
    @Test
    public void findAllIdentifieres_Test() throws IOException, SAXException {
        Response response = controller.listIdentifieres(appContext);
    }
    
    @Test
    public void updateIdentifieres_Test() throws ParseException, Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(identifiers());
        IdentifieresController ic = PowerMockito.spy(controller);
        // convert json string to set with identifieres pojo objects
        when(ic, method(IdentifieresController.class, "buildSqlObjects", String.class)).withArguments(json).thenCallRealMethod();
        // mock the save identifieres data in database privat method
        PowerMockito.doNothing().when(ic, method(IdentifieresController.class, "saveIdentifieres", Set.class)).withArguments(identifiers());
        
        ic.updateIdentifieres(json);
    }
    
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(IdentifieresController.class);
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }
    
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return super.getTestContainerFactory();
    }
    
    private Set<Identifier> identifiers() throws ParseException {
        Set<Identifier> identifiers = new HashSet<>();
        Identifier id1 = new Identifier();
        id1.setIdentifier("oai:example.org:qucosa:48672");
        id1.setDatestamp(DateTimeConverter.timestampWithTimezone("2017-12-14T09:42:45Z"));
        
        Identifier id2 = new Identifier();
        id2.setIdentifier("oai:example.org:qucosa:48661");
        id2.setDatestamp(DateTimeConverter.timestampWithTimezone("2018-01-09T16:47:36Z"));
        
        Identifier id3 = new Identifier();
        id3.setIdentifier("oai:example.org:qucosa:48668");
        id3.setDatestamp(DateTimeConverter.timestampWithTimezone("2017-12-14T09:42:23Z"));
        
        identifiers.add(id1);
        identifiers.add(id2);
        identifiers.add(id3);
        
        return identifiers;
    }
}
