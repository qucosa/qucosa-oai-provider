/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
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

import de.qucosa.oai.provider.persistence.model.Identifiable;

import java.util.Collection;

public interface Dao<T extends Identifiable> {

    T saveAndSetIdentifier(T object);

    Collection<T> saveAndSetIdentifier(Collection<T> objects);

    T update(T object);

    Collection<T> update();

    /**
     * Find all records.
     *
     * @return Collection of all record in the database.
     */
    Collection<T> findAll();

    T findById(String id);

    Collection<T> findByPropertyAndValue(String property, String value);

    T findByMultipleValues(String clause, String... values);

    Collection<T> findRowsByMultipleValues(String clause, String... values);

//    Collection<T> findLastRowsByProperty();

    Collection<T> findFirstRowsByProperty(String property, int limit);

    void delete();

    void delete(String ident);

    void delete(T object);
}


