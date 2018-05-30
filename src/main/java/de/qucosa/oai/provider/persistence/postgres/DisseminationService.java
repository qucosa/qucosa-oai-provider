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

package de.qucosa.oai.provider.persistence.postgres;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Disemination;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Set;

public class DisseminationService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @Override
    public Set<Disemination> findAll() {return null; }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @Override
    public <T> void update(T sets) {
        Set<Disemination> records = (Set<Disemination>) sets;
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO records (id, identifier_id, format, moddate, xmldata \r\n)");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (id) DO UPDATE \r\n");
        sb.append("SET identifier_id = ?, format = ?, moddate = ?, xmldata = ?;");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (Disemination record : records) {
                StringWriter sw = new StringWriter();
                sw.write(record.getXmldata());
                SQLXML sqlxml = connection().createSQLXML();
                sqlxml.setString(sw.toString());
                
                pst.setLong(1, record.getIdentifierId());
                pst.setLong(2, record.getFormat());
                pst.setDate(3, record.getModdate());
                pst.setSQLXML(4, sqlxml);
                
                pst.setLong(5, record.getIdentifierId());
                pst.setLong(6, record.getFormat());
                pst.setDate(7, record.getModdate());
                pst.setSQLXML(8, sqlxml);
                
                pst.addBatch();
            }
            
            pst.executeBatch();
            connection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T findById(Long id) {
        return null;
    }

    @Override
    public <T> T findByValues(Set<T> values) {
        return null;
    }

    @Override
    public void deleteById(Long id) {}

    @Override
    public <T> void deleteByKeyValue(String key, T value) { }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public <T> void deleteByValues(Set<T> values) {}

    @Override
    public <T> T findByValue(String column, String value) { return null; }

    @Override
    public void update(String sql) { }

    @Override
    public void update(String... value) { }

    @Override
    public <T> T findByValues(String... values) { return null; }

    @Override
    public <T> T findByIds(T... values) { return null; }
}
