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
import de.qucosa.oai.provider.persistence.pojos.Dissemination;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Set;

public class DisseminationDao extends PersistenceServiceAbstract implements PersistenceServiceInterface {

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
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @Override
    public <T> void update(T object) throws SQLException {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO disseminations (id, id_record, id_format, lastmoddate, xmldata \r\n)");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (id) DO UPDATE \r\n");
        sb.append("SET id_record = ?, id_format = ?, lastmoddate = ?, xmldata = ?;");

        PreparedStatement pst = connection().prepareStatement(sb.toString());
        connection().setAutoCommit(false);

        if (object instanceof Set) {
            Set<Dissemination> records = (Set<Dissemination>) object;

            for (Dissemination dissemination : records) {
                buildUpdateObject(pst, dissemination);
            }
        }

        if (object instanceof Dissemination) {
            buildUpdateObject(pst, (Dissemination) object);
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

    private void buildUpdateObject(PreparedStatement pst, Dissemination dissemination) throws SQLException {
        StringWriter sw = new StringWriter();
        sw.write(dissemination.getXmldata());
        SQLXML sqlxml = connection().createSQLXML();
        sqlxml.setString(sw.toString());

        pst.setLong(1, dissemination.getRecordId());
        pst.setLong(2, dissemination.getFormatId());
        pst.setDate(3, dissemination.getModdate());
        pst.setSQLXML(4, sqlxml);

        pst.setLong(5, dissemination.getRecordId());
        pst.setLong(6, dissemination.getFormatId());
        pst.setDate(7, dissemination.getModdate());
        pst.setSQLXML(8, sqlxml);

        pst.addBatch();
    }
}
