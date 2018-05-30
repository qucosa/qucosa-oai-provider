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

package de.qucosa.oai.provider.persistence.service.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.application.ApplicationBinder;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.postgres.RecordService;

public class Identifieres_IT extends JerseyTest {
    @Context
    private ServletContext context;
    
    private Connection connection = null;
    
    private PersistenceServiceInterface service = new RecordService();
    
    @Before
    @Override
    public void setUp() {
        ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
        locator.inject(this);
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Test
    public void findAll_Test() {
        Set<Record> identifiers = service.findAll();
        identifiers.size();
    }
    
    @Test
    public void find_Test() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT id, identifier, datestamp, SUBSTRING(identifier, 'qucosa:\\d+$') AS pid \r\n");
        sb.append("FROM identifier WHERE identifier ~ 'qucosa:\\d+$';");
        Set<Record> identifiers = service.find(sb.toString());
        identifiers.size();
    }
    
    @Test
    public void loadIdentifieresFromFedora_Test() {
        Set<Record> identifiers = new HashSet<>();
    }
    
    @After
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
