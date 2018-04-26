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

import java.sql.Connection;
import java.util.Set;

public interface PersistenceServiceInterface {
    public void setConnection(Connection connection);
    
    public <T> Set<T> findAll();
    
    public <T> Set<T> find(String sqlStmt);
    
    public <T> void update(Set<T> sets);
    
    public void update(String sql);
    
    public void update(String...value);
    
    public <T> T findById(Long id);
    
    @SuppressWarnings("unchecked")
    public <T> T findByIds(T...values);
    
    public <T> T findByValue(String column, String value);
    
    public <T> T findByValues(Set<T> values);
    
    public <T> T findByValues(String...values);
    
    public void deleteById(Long id);
    
    public <T> void deleteByValues(Set<T> values);
}
