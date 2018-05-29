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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;

public class FormatService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Format> findAll() {
        Set<Format> formats = new HashSet<>();
        ResultSet result = null;
        String sql = "SELECT * FROM formats;";

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                Format format = new Format();
                format.setId(result.getLong("id"));
                format.setMdprefix(result.getString("mdprefix"));
                format.setDissType(result.getString("disstpye"));
                format.setLastpolldate(result.getTimestamp("lastpolldate"));
                formats.add(format);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return formats;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void update(T sets) {
        Set<Format> formats = (Set<Format>) sets;
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO formats (id, mdprefix, disstype, lastpolldate) \r\n");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (mdprefix) \r\n");
        sb.append("DO UPDATE SET mdprefix = ?, disstype = ?, lastpolldate = ?; \r\n");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (Format format : formats) {
                pst.setString(1, format.getMdprefix());
                pst.setString(2, format.getDissType());
                pst.setTimestamp(3, format.getLastpolldate());
                pst.setString(4, format.getMdprefix());
                pst.setString(5, format.getDissType());
                pst.setTimestamp(6, format.getLastpolldate());
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
    public <T> void deleteByValues(Set<T> values) {}

    @Override
    public <T> T findByValue(String column, String value) {
        Format format =  new Format();
        StringBuffer sb = new StringBuffer();
        sb.append("select id, mdprefix, lastpolldate, disstype from formats where " + column + " = ?;");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            pst.setString(1, value);
            ResultSet result = pst.executeQuery();
            
            while(result.next()) {
                format.setId(result.getLong("id"));
                format.setMdprefix(result.getString("mdprefix"));
                format.setDissType(result.getString("disstype"));
                format.setLastpolldate(result.getTimestamp("lastpolldate"));
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return (T) format;
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
    public <T> void deleteByKeyValue(String key, T value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteByKeyValue(String... paires) {
        // TODO Auto-generated method stub
        
    }
}
