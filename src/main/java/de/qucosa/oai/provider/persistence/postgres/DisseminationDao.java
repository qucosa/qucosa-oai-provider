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
import de.qucosa.oai.provider.persistence.pojos.Dissemination;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Set;

public class DisseminationDao extends PersistenceDaoAbstract implements PersistenceDao {

    @Override
    public <T> T create(T object) { return null; }

    @Override
    public Set<Dissemination> findAll() {return null; }

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) {
        return 0;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) throws SQLException { return null; }

    @Override
    public <T> T update(T object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;
        PreparedStatement select = connection().prepareCall("SELECT id FROM disseminations WHERE id_record = ? AND id_format = ?;");
        connection().setAutoCommit(false);
        select.setLong(1, dissemination.getRecordId());
        select.setLong(2, dissemination.getFormatId());
        ResultSet rows = select.executeQuery();

        while (rows.next()) {
            dissemination.setId(rows.getLong("id"));
        }

        rows.close();

        StringBuffer sb = new StringBuffer();

        if (dissemination.getId() != null) {
            sb.append("UPDATE disseminations SET lastmoddate = ?, xmldata = ? WHERE id = ?;");
        } else {
            sb.append("INSERT INTO disseminations (id, id_record, id_format, lastmoddate, xmldata \r\n)");
            sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?, ?) \r\n");
        }

        PreparedStatement pst = connection().prepareStatement(sb.toString());
        connection().setAutoCommit(false);
        buildUpdateObject(pst, dissemination);
        connection().commit();
        int affectedRows = pst.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating user failed, no rows affected.");
        }

        try (ResultSet generatedKeys = pst.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

            dissemination.setId(generatedKeys.getLong(1));
        }

        return (T) dissemination;
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
    public <T> T update(String sql) { return null; }

    @Override
    public <T> T update(String... value) { return null; }

    @Override
    public <T> T findByValues(String... values) { return null; }

    @Override
    public <T> T findByIds(T... values) { return null; }

    private void buildUpdateObject(PreparedStatement pst, Dissemination dissemination) throws SQLException {
        StringWriter sw = new StringWriter();
        sw.write(dissemination.getXmldata());
        SQLXML sqlxml = connection().createSQLXML();
        sqlxml.setString(sw.toString());

        if (dissemination.getId() != null) {
            pst.setTimestamp(1, dissemination.getLastmoddate());
            pst.setSQLXML(2, sqlxml);
            pst.setLong(3, dissemination.getId());
        } else {
            pst.setLong(1, dissemination.getRecordId());
            pst.setLong(2, dissemination.getFormatId());
            pst.setTimestamp(3, dissemination.getLastmoddate());
            pst.setSQLXML(4, sqlxml);
        }
    }

    @Override
    public void runProcedure() { }
}
