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

package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.RstToIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class RstToIdentifiersDao<T extends RstToIdentifiers> implements Dao<RstToIdentifiers> {
    private Connection connection;

    @Autowired
    public RstToIdentifiersDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public RstToIdentifiersDao() {
        this.connection = null;
    }

    @Override
    public RstToIdentifiers saveAndSetIdentifier(RstToIdentifiers object) throws SaveFailed {
        return null;
    }

    @Override
    public Collection<RstToIdentifiers> saveAndSetIdentifier(Collection<RstToIdentifiers> objects) throws SaveFailed {
        String sql = "INSERT INTO rst_to_identifiers (record_id, rst_id)" +
                " VALUES (?, ?)";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            for (RstToIdentifiers rstToIdentifiers : objects) {
                pst.clearParameters();
                pst.setLong(1, rstToIdentifiers.getRecordId());
                pst.setString(2, rstToIdentifiers.getRstId());
                pst.addBatch();
            }

            pst.clearParameters();
            int[] insertRows = pst.executeBatch();

            if (insertRows.length != objects.size()) {
                throw new SaveFailed("Not all rows saved.");
            }

            connection.commit();
            pst.close();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return null;
    }

    @Override
    public RstToIdentifiers update(RstToIdentifiers object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<RstToIdentifiers> update(Collection<RstToIdentifiers> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<RstToIdentifiers> findAll() throws NotFound {
        return null;
    }

    @Override
    public RstToIdentifiers findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<RstToIdentifiers> findByPropertyAndValue(String property, String value) throws NotFound {
        return null;
    }

    @Override
    public RstToIdentifiers findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<RstToIdentifiers> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public RstToIdentifiers findLastRowsByProperty(String property, boolean limit) {
        return null;
    }

    @Override
    public RstToIdentifiers findFirstRowsByProperty(String property, boolean limit) {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) throws DeleteFailed {

    }

    @Override
    public void delete(RstToIdentifiers object) throws DeleteFailed {

    }
}
