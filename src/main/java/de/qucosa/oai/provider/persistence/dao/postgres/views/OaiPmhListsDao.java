/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.persistence.dao.postgres.views;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhLists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.util.Collection;

@Repository
public class OaiPmhListsDao<T extends OaiPmhLists> implements Dao<OaiPmhLists> {

    private Connection connection;

    @Autowired
    public OaiPmhListsDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public OaiPmhListsDao() { }

    @Override
    public OaiPmhLists saveAndSetIdentifier(OaiPmhLists object) throws SaveFailed {
        return null;
    }

    @Override
    public Collection<OaiPmhLists> saveAndSetIdentifier(Collection<OaiPmhLists> objects) throws SaveFailed {
        return null;
    }

    @Override
    public OaiPmhLists update(OaiPmhLists object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<OaiPmhLists> update(Collection<OaiPmhLists> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<OaiPmhLists> findAll() throws NotFound {
        return null;
    }

    @Override
    public OaiPmhLists findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<OaiPmhLists> findByPropertyAndValue(String property, String value) throws NotFound {
        return null;
    }

    @Override
    public OaiPmhLists findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public void delete(String ident) throws DeleteFailed {

    }

    @Override
    public void undoDelete(String ident) throws UndoDeleteFailed {

    }

    @Override
    public void delete(OaiPmhLists object) throws DeleteFailed {

    }

    @Override
    public void undoDelete(OaiPmhLists object) throws UndoDeleteFailed {

    }
}
