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

package de.qucosa.oai.provider.persistence;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public interface PersistenceDaoInterface {

    <T> T create(T object) throws SQLException;

    <T> T update(String sql);

    <T> T update(String...value);

    <T> T update(T object) throws SQLException;

    <T> Set<T> findAll();

    <T> Set<T> find(String sqlStmt) throws SQLException;

    <T> T findById(Long id);
    
    <T> T findByIds(T...values);
    
    <T> T findByValue(String column, String value) throws SQLException;
    
    <T> T findByValues(Set<T> values);
    
    <T> T findByValues(String...values);
    
    void deleteById(Long id);

    <T> void deleteByKeyValue(String key, T value) throws SQLException;

    void deleteByKeyValue(String... paires);
    
    <T> void deleteByValues(Set<T> values);

    int count(String cntField, String... whereClauses);

    int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException;

    void runProcedure() throws SQLException;
}
