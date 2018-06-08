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
import de.qucosa.oai.provider.persistence.pojos.Record;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class RecordDao extends PersistenceDaoAbstract implements PersistenceDaoInterface {
    @Override
    public Set<Record> findAll() { return null; }
    
    @Override
    public <T> int[] update(T data) throws SQLException {
        String sql = "INSERT INTO records (id, pid, datestamp) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?) \r\n";
        sql+="ON CONFLICT (pid) \r\n";
        sql+="DO UPDATE SET pid = ? \r\n";
        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);

        if (data instanceof Set) {
            Set<Record> records = (Set<Record>) data;

            for (Record record : records) {
                buildUpdateObject(pst, record);
            }
        }

        if (data instanceof Record) {
            buildUpdateObject(pst, (Record) data);
        }

        int[] ex = pst.executeBatch();
        connection().commit();
        return ex;
    }

    @Override
    public <T> T findById(Long id) { return null; }

    @Override
    public <T> T findByValues(Set<T> values) { return null; }

    @Override
    public void deleteById(Long id) { }

    @Override
    public <T> void deleteByValues(Set<T> values) { }

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) { return 0; }

    @Override
    public <T> Set<T> find(String sqlStmt) { return null; }

    @Override
    public <T> T findByValue(String column, String value) throws SQLException {
        Record record = new Record();
        String sql = "SELECT id, pid, datestamp, deleted FROM records WHERE " + column + " = ?";
        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);
        pst.setString(1, value);
        ResultSet resultSet = pst.executeQuery();

        while (resultSet.next()) {
            record.setPid(resultSet.getString("pid"));
            record.setId(resultSet.getLong("id"));
            record.setDatestamp(resultSet.getTimestamp("datestamp"));
            record.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();

        return (T) record;
    }

    @Override
    public int[] update(String sql) { return null; }

    @Override
    public int[] update(String... value) { return null; }

    @Override
    public <T> T findByValues(String... values) { return null; }

    @Override
    public <T> T findByIds(T... values) { return null; }

    @Override
    public <T> void deleteByKeyValue(String key, T value) throws SQLException {
        String sql = "UPDATE records SET deleted = true WHERE " + key + " = ?";
        PreparedStatement pst = connection().prepareStatement(sql);
        connection().setAutoCommit(false);

        pst.setString(1, (String) value);
        pst.addBatch();

        pst.executeBatch();
        connection().commit();
    }

    @Override
    public void deleteByKeyValue(String... paires) { }

    private void buildUpdateObject(PreparedStatement pst, Record record) throws SQLException {
        pst.setString(1, record.getPid());
        pst.setTimestamp(2, record.getDatestamp());
        pst.setString(3, record.getPid());
        pst.addBatch();
    }

    @Override
    public void runProcedure() { }
}
