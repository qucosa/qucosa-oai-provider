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
import de.qucosa.oai.provider.persistence.pojos.Format;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class FormatDao extends PersistenceDaoAbstract implements PersistenceDao {

    @Override
    public <T> T create(T object) { return null; }

    @Override
    public Set<Format> findAll() {
        Set<Format> formats = new HashSet<>();
        ResultSet result;
        String sql = "SELECT * FROM formats;";

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                Format format = new Format();
                format.setId(result.getLong("id"));
                format.setMdprefix(result.getString("mdprefix"));
                format.setSchemaUrl(result.getString("schemaurl"));
                format.setNamespace(result.getString("namespace"));
                format.setDeleted(result.getBoolean("deleted"));
                formats.add(format);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return formats;
    }

    @Override
    public int count(String cntField, String... whereClauses) { return 0; }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException { return 0; }

    @Override
    public <T> Set<T> find(String sqlStmt) { return null; }

    @Override
    public <T> T update(T object) throws SQLException {
        Format format = (Format) object;

        if (format.getSchemaUrl() == null || format.getNamespace() == null || format.getMdprefix() == null) {
            throw new SQLException("Unauthorized null values in format object.");
        }

        if(format.getMdprefix().isEmpty() || format.getNamespace().isEmpty() || format.getSchemaUrl().isEmpty()) {
            throw new SQLException("Unauthorized empty values in format object.");
        }

        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n";
        sql+="ON CONFLICT (mdprefix) \r\n";
        sql+="DO UPDATE SET schemaurl = ?, namespace = ?; \r\n";
        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);
        buildUpdateObject(pst, (Format) object);
        connection().commit();

        int affectedRows = pst.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating format failed, no rows affected.");
        }

        try (ResultSet generatedKeys = pst.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating format failed, no ID obtained.");
            }

            format.setId(generatedKeys.getLong(1));
        }

        return (T) format;
    }

    @Override
    public <T> T findById(Long id) { return null; }

    @Override
    public <T> T findByValues(Set<T> values) { return null; }

    @Override
    public void deleteById(Long id) {}

    @Override
    public <T> void deleteByValues(Set<T> values) {}

    @Override
    public <T> T findByValue(String column, String value) throws SQLException {
        Format format =  new Format();
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats WHERE " + column + " = ?;";
        
        try {
            PreparedStatement pst = connection().prepareStatement(sql);
            connection().setAutoCommit(false);
            pst.setString(1, value);
            ResultSet result = pst.executeQuery();
            
            while(result.next()) {
                format.setId(result.getLong("id"));
                format.setMdprefix(result.getString("mdprefix"));
                format.setSchemaUrl(result.getString("schemaurl"));
                format.setNamespace(result.getString("namespace"));
                format.setDeleted(result.getBoolean("deleted"));
            }
            
            result.close();
        } catch (SQLException e) {
            throw new SQLException("Cannot find format object.");
        }
        
        return (T) format;
    }

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
        String sql = "UPDATE formats SET deleted = true WHERE " + key + " = ?";
        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);
        pst.setString(1, (String) value);
        connection().commit();
        int affectedRows = pst.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Cannot format mark as deleted, no rows affected.");
        }
    }

    @Override
    public void deleteByKeyValue(String... paires) {}

    private void buildUpdateObject(PreparedStatement pst, Format format) throws SQLException {
        pst.setString(1, format.getMdprefix());
        pst.setString(2, format.getSchemaUrl());
        pst.setString(3, format.getNamespace());
        pst.setString(4, format.getSchemaUrl());
        pst.setString(5, format.getNamespace());
    }

    @Override
    public void runProcedure() { }
}
