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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface PersistenceServiceInterface {
    void setConnection(Connection connection);
    
    <T> Set<T> findAll();
    
    <T> Set<T> find(String sqlStmt);
    
    void update(String sql);
    
    void update(String...value);

    <T> void update(T object) throws SQLException, IOException, SAXException;

    <T> T findById(Long id);
    
    <T> T findByIds(T...values);
    
    <T> T findByValue(String column, String value);
    
    <T> T findByValues(Set<T> values);
    
    <T> T findByValues(String...values);
    
    void deleteById(Long id);

    <T> void deleteByKeyValue(String key, T value);

    void deleteByKeyValue(String... paires);
    
    <T> void deleteByValues(Set<T> values);
}
