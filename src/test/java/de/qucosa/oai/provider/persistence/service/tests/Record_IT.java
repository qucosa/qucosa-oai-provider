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
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.postgres.DisseminationDao;

public class Record_IT {
    Connection connection = null;
    
    PersistenceServiceInterface service = new DisseminationDao();
    
    @Before
    public void connect() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Test
    public void findAll_Test() {
        Set<Dissemination> records = service.findAll();
        records.size();
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
