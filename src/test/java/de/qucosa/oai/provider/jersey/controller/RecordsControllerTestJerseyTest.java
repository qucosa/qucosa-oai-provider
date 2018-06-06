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

import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.text.ParseException;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.ApplicationBinder;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.utils.DateTimeConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RecordController.class)
public class RecordsControllerTestJerseyTest extends AbstractJerseyTest {
    @Inject
    private RecordController controller;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Binder binder = new AbstractBinder() {
            
            @Override
            protected void configure() {
                bindAsContract(RecordController.class);
            }
        };
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(binder, new ApplicationBinder());
        locator.inject(this);
    }
    
    @Test
    public void updateIdentifieres_Test() throws ParseException, Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(identifiers());
        RecordController ic = PowerMockito.spy(controller);
        // convert json string to set with identifieres pojo objects
        doCallRealMethod()
                .when(ic, method(RecordController.class, "buildSqlObjects", String.class))
                .withArguments(json);
        // mock the save identifieres data in database privat method
        doNothing()
                .when(ic, method(RecordController.class, "saveIdentifieres", Set.class))
                .withArguments(identifiers());
        
        ic.save(json);
    }
    
    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(RecordController.class);
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }
    
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return super.getTestContainerFactory();
    }
    
    private Set<Record> identifiers() throws ParseException {
        Set<Record> identifiers = new HashSet<>();
        Record id1 = new Record();
        id1.setPid("qucosa:48672");
        id1.setDatestamp(DateTimeConverter.timestampWithTimezone("2017-12-14T09:42:45Z"));
        
        Record id2 = new Record();
        id2.setPid("qucosa:48661");
        id2.setDatestamp(DateTimeConverter.timestampWithTimezone("2018-01-09T16:47:36Z"));
        
        Record id3 = new Record();
        id3.setPid("qucosa:48668");
        id3.setDatestamp(DateTimeConverter.timestampWithTimezone("2017-12-14T09:42:23Z"));
        
        identifiers.add(id1);
        identifiers.add(id2);
        identifiers.add(id3);
        
        return identifiers;
    }
}
