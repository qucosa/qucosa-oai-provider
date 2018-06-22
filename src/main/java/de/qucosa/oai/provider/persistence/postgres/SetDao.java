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
import de.qucosa.oai.provider.persistence.PersistenceDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class SetDao extends PersistenceDaoAbstract implements PersistenceDao {

    @Override
    public <T> T create(T object) throws SQLException {
        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n";
        sql+="ON CONFLICT (setspec) \r\n";
        sql+="DO NOTHING";

        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);

        if (object instanceof Set) {

            for (de.qucosa.oai.provider.persistence.pojos.Set set : (Set<de.qucosa.oai.provider.persistence.pojos.Set>) object) {
                pst.setString(1, set.getSetSpec());
                pst.setString(2, set.getSetName());
                pst.setString(3, set.getSetDescription());
                pst.addBatch();
            }
        }

        if (object instanceof de.qucosa.oai.provider.persistence.pojos.Set) {
            de.qucosa.oai.provider.persistence.pojos.Set set = (de.qucosa.oai.provider.persistence.pojos.Set) object;
            pst.setString(1, set.getSetSpec());
            pst.setString(2, set.getSetName());
            pst.setString(3, set.getSetDescription());
            pst.addBatch();
        }

        pst.executeBatch();
        connection().commit();
        return object;
    }
    
    public Set<de.qucosa.oai.provider.persistence.pojos.Set> findAll() {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        ResultSet result;
        String sql = "SELECT id, setspec, setname, setdescription, deleted FROM sets;";

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                de.qucosa.oai.provider.persistence.pojos.Set set = new de.qucosa.oai.provider.persistence.pojos.Set();
                set.setId(result.getLong("id"));
                set.setSetSpec(result.getString("setspec"));
                set.setSetName(result.getString("setname"));
                set.setSetDescription(result.getString("setdescription"));
                set.setDeleted(result.getBoolean("deleted"));
                sets.add(set);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sets;
    }
    
    @Override
    public <T> T update(T object) throws SQLException {
        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n";
        sql+="ON CONFLICT (setspec) \r\n";
        sql+="DO UPDATE SET setname = ?, setdescription = ? \r\n";

        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);

        if (object instanceof Set) {

            for (de.qucosa.oai.provider.persistence.pojos.Set set : (Set<de.qucosa.oai.provider.persistence.pojos.Set>) object) {
                pst.setString(1, set.getSetSpec());
                pst.setString(2, set.getSetName());
                pst.setString(3, set.getSetDescription());
                pst.setString(4, set.getSetName());
                pst.setString(5, set.getSetDescription());
                pst.addBatch();
            }
        }

        if (object instanceof de.qucosa.oai.provider.persistence.pojos.Set) {
            de.qucosa.oai.provider.persistence.pojos.Set set = (de.qucosa.oai.provider.persistence.pojos.Set) object;
            pst.setString(1, set.getSetSpec());
            pst.setString(2, set.getSetName());
            pst.setString(3, set.getSetDescription());
            pst.setString(4, set.getSetName());
            pst.setString(5, set.getSetDescription());
            pst.addBatch();
        }

        pst.executeBatch();
        connection().commit();
        return object;
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
    public <T> T findByValue(String column, String value) { return null; }

    @Override
    public <T> T update(String sql) { return null; }

    @Override
    public <T> T update(String... value) { return null; }

    @Override
    public <T> T findByValues(String... values) { return null; }

    @Override
    public <T> T findByIds(T... values) { return null; }

    @Override
    public <T> void deleteByKeyValue(String key, T value) throws SQLException {
        String sql = "UPDATE sets SET deleted = true WHERE " + key + " = " + value;
        Statement stmt = connection().createStatement();
        stmt.executeUpdate(sql);
        connection().close();
    }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public void runProcedure() { }
}
