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

import java.sql.SQLException;
import java.util.Set;

public class SetsToRecordDao<T> implements PersistenceDao<T> {

    @Override
    public T create(T object) { return null; }

    @Override
    public int count(String cntField, String... whereClauses) { return 0; }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException { return 0; }

    @Override
    public T findAll() { return null; }

    @Override
    public T find(String sqlStmt) { return null; }

    @Override
    public T update(String sql) { return null; }

    @Override
    public T update(String... value) { return null; }

    @Override
    public T update(T object) { return null; }

    @Override
    public T findById(Long id) { return null; }

    @Override
    public T findByIds(T... values) { return null; }

    @Override
    public T findByValue(String column, String value) throws SQLException { return null; }

    @Override
    public T findByValues(Set<T> values) { return null; }

    @Override
    public T findByValues(String... values) { return null; }

    @Override
    public void deleteById(Long id) { }

    @Override
    public void deleteByKeyValue(String key, T value) throws SQLException { }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public void deleteByValues(Set<T> values) { }

    @Override
    public void runProcedure() throws SQLException { }
}
