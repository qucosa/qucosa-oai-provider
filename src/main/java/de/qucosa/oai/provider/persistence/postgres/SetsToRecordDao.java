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
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Set;

public class SetsToRecordDao extends PersistenceDaoAbstract implements PersistenceDaoInterface {

    @Override
    public <T> T create(T object) { return null; }

    @Override
    public int count(String cntField, String... whereClauses) { return 0; }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException { return 0; }

    @Override
    public <T> Set<T> findAll() { return null; }

    @Override
    public <T> Set<T> find(String sqlStmt) { return null; }

    @Override
    public <T> T update(String sql) { return null; }

    @Override
    public <T> T update(String... value) { return null; }

    @Override
    public <T> T update(T object) { return null; }

    @Override
    public <T> T findById(Long id) { return null; }

    @Override
    public <T> T findByIds(T... values) { return null; }

    @Override
    public <T> T findByValue(String column, String value) throws SQLException { return null; }

    @Override
    public <T> T findByValues(Set<T> values) { return null; }

    @Override
    public <T> T findByValues(String... values) { return null; }

    @Override
    public void deleteById(Long id) { }

    @Override
    public <T> void deleteByKeyValue(String key, T value) throws SQLException { }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public <T> void deleteByValues(Set<T> values) { }

    @Override
    public void runProcedure() throws SQLException {
        String sql = "{call generate_sets_to_records()}";
        CallableStatement cst = connection().prepareCall(sql);
        cst.execute();
        cst.close();
    }
}
