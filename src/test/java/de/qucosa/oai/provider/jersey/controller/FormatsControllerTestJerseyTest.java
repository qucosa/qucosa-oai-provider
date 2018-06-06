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
import de.qucosa.oai.provider.controller.FormatsController;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.postgres.FormatDao;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;


//@RunWith(PowerMockRunner.class)
//@PrepareForTest(FormatsController.class)
public class FormatsControllerTestJerseyTest extends JerseyTest {
    private PersistenceDaoInterface formatDao;

    private FormatsController formatsController;
    
//    @Before
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        Binder binder = new AbstractBinder() {
//
//            @Override
//            protected void configure() {
//                bindAsContract(FormatsController.class);
//            }
//        };
//
//        ServiceLocator locator = ServiceLocatorUtilities.bind(binder, new ApplicationBinder());
//        locator.inject(this);
//    }
    
    @Test
    public void updateFormats_Test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(format());
        doNothing().when(formatDao.update(format()));
    }

    @Override
    protected Application configure() {
        formatDao = mock(FormatDao.class);
        formatsController = new FormatsController((FormatDao) formatDao);

        ResourceConfig config = new ResourceConfig(FormatsController.class);
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }

    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("formats").build();
    }

    //    @Override
//    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
//        return super.getTestContainerFactory();
//    }
    
    private Format format() {
        Format fm = new Format();
        fm.setMdprefix("xmetadiss");
        fm.setLastpolldate(new Timestamp(new Date().getTime()));
        return fm;
    }
}
