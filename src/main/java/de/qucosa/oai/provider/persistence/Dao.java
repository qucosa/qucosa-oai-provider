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

import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.HasIdentifier;

import java.util.Collection;

@SuppressWarnings("RedundantThrows")
public interface Dao<T extends HasIdentifier> {

    T saveAndSetIdentifier(T object) throws SaveFailed;

    Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed;

    T update(T object) throws UpdateFailed;

    Collection<T> update(Collection<T> objects) throws UpdateFailed;

    Collection<T> findAll() throws NotFound;

    T findById(String id) throws NotFound;

    Collection<T> findByPropertyAndValue(String property, String value) throws NotFound;

    T findByMultipleValues(String clause, String... values) throws NotFound;

    Collection<T> findRowsByMultipleValues(String clause, String... values) throws NotFound;

    Collection<T> findLastRowsByProperty(String property, int limit) throws NotFound;

    Collection<T> findFirstRowsByProperty(String property, int limit) throws NotFound;

    void delete() throws DeleteFailed;

    void delete(String ident) throws DeleteFailed;

    void delete(T object) throws DeleteFailed;
}


