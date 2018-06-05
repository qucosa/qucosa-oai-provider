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

import de.qucosa.oai.provider.persistence.PersistenceDaoAbstract;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SetDao extends PersistenceDaoAbstract implements PersistenceDaoInterface {
    
    public Set<de.qucosa.oai.provider.persistence.pojos.Set> findAll() {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        ResultSet result;
        String sql = "SELECT id, setspec, predicate, doc, XPATH('//setSpec', doc) AS setspecnode FROM sets;";

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                de.qucosa.oai.provider.persistence.pojos.Set set = new de.qucosa.oai.provider.persistence.pojos.Set();
                set.setId(result.getLong("id"));
                set.setSetSpec(result.getString("setspec"));
                set.setPredicate(result.getString("predicate"));
                set.setDoc(result.getString("doc"));
                sets.add(set);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sets;
    }
    
    @Override
    public <T> void update(T object) throws SQLException, IOException, SAXException {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO sets (id, setspec, predicate, doc) \r\n");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (setspec) \r\n");
        sb.append("DO UPDATE SET doc = ?, predicate = ? \r\n");

        PreparedStatement pst = connection().prepareStatement(sb.toString());
        connection().setAutoCommit(false);
        SQLXML sqlxml = connection().createSQLXML();

        if (object instanceof Set) {

            for (de.qucosa.oai.provider.persistence.pojos.Set set : (Set<de.qucosa.oai.provider.persistence.pojos.Set>) object) {
                sqlxml.setString(DocumentXmlUtils.resultXml(set.getDocument()));
                pst.setString(1, set.getSetSpec());
                pst.setString(2, set.getPredicate());
                pst.setSQLXML(3, sqlxml);
                pst.setSQLXML(4, sqlxml);
                pst.setString(5, set.getPredicate());
                pst.addBatch();
            }
        }

        if (object instanceof de.qucosa.oai.provider.persistence.pojos.Set) {
            de.qucosa.oai.provider.persistence.pojos.Set set = (de.qucosa.oai.provider.persistence.pojos.Set) object;
            sqlxml.setString(DocumentXmlUtils.resultXml(set.getDocument()));
            pst.setString(1, set.getSetSpec());
            pst.setString(2, set.getPredicate());
            pst.setSQLXML(3, sqlxml);
            pst.setSQLXML(4, sqlxml);
            pst.addBatch();
        }

        pst.executeBatch();
        connection().commit();
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
    public <T> void deleteByValues(Set<T> values) {}

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) {
        return 0;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @Override
    public <T> T findByValue(String column, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(String sql) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(String... value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public <T> T findByValues(String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T findByIds(T... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> void deleteByKeyValue(String key, T value) throws SQLException {
        String sql = "UPDATE sets SET deleted = true WHERE " + key + " = " + value;
        Statement stmt = connection().createStatement();
        stmt.executeUpdate(sql);
        connection().close();
    }

    @Override
    public void deleteByKeyValue(String... paires) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void runProcedure() { }
}
