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

package de.qucosa.oai.provider.jersey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.SetController;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SetController.class)
public class SetsControllerTestJerseyTest extends AbstractJerseyTest {
    @Inject
    private SetController setsController;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        Binder binder = new AbstractBinder() {
            
            @Override
            protected void configure() {
                bindAsContract(SetController.class);
            }
        };
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(binder);
        locator.inject(this);
    }
    
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(SetController.class);
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }
    
    @Test
    public void updateSets_Test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        File setSpecs = new File("/home/opt/oaiprovider/config/list-set-conf.json");
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        
        try {
            json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        SetController sc = spy(setsController);
        doCallRealMethod().when(sc, MemberMatcher.method(SetController.class, "buildSqlSets", String.class))
            .withArguments(om.writeValueAsString(json));
        doNothing().when(sc, MemberMatcher.method(SetController.class, "saveSetSpecs", Set.class))
            .withArguments(json);
        sc.update("", om.writeValueAsString(json));
    }
}
