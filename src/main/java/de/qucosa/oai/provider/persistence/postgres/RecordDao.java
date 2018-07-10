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

import de.qucosa.oai.provider.persistence.PersistenceDao;
import de.qucosa.oai.provider.persistence.pojos.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class RecordDao<T> implements PersistenceDao<T> {

    private Connection connection;

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T create(T object) { return null; }

    @Override
    public T findAll() { return null; }
    
    @Override
    public T update(T object) throws SQLException {
        String sql = "INSERT INTO records (id, pid, uid) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?) \r\n";
        sql+="ON CONFLICT (uid) \r\n";
        sql+="DO UPDATE SET uid = ?\r\n";
        PreparedStatement pst = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        buildUpdateObject(pst, (Record) object);
        connection.commit();
        int affectedRows = pst.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Update record failed, no rows affected.");
        }

        return object;
    }

    @Override
    public T findById(Long id) { return null; }

    @Override
    public T findByValues(Set<T> values) { return null; }

    @Override
    public void deleteById(Long id) { }

    @Override
    public void deleteByValues(Set<T> values) { }

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) { return 0; }

    @Override
    public T find(String sqlStmt) { return null; }

    @Override
    public T findByValue(String column, String value) throws SQLException {
        Record record = new Record();
        String sql = "SELECT id, pid, uid, deleted FROM records WHERE " + column + " = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        pst.setString(1, value);
        ResultSet resultSet = pst.executeQuery();

        if (resultSet.getFetchSize() == 0) {
            resultSet.close();
            throw new SQLException("Record not found.");
        }

        while (resultSet.next()) {
            record.setPid(resultSet.getString("pid"));
            record.setUid(resultSet.getString("uid"));
            record.setId(resultSet.getLong("id"));
            record.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();

        return (T) record;
    }

    @Override
    public T update(String sql) { return null; }

    @Override
    public T update(String... value) { return null; }

    @Override
    public T findByValues(String... values) { return null; }

    @Override
    public T findByIds(T... values) { return null; }

    @Override
    public void deleteByKeyValue(String key, T value) throws SQLException {
        String sql = "UPDATE records SET deleted = true WHERE " + key + " = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        connection.setAutoCommit(false);

        pst.setString(1, (String) value);
        pst.addBatch();

        pst.executeBatch();
        connection.commit();
    }

    @Override
    public void deleteByKeyValue(String... paires) { }

    private void buildUpdateObject(PreparedStatement pst, Record record) throws SQLException {
        pst.setString(1, record.getPid());
        pst.setString(2, record.getUid());
        pst.setString(3, record.getPid());
    }

    @Override
    public void runProcedure() { }
}
