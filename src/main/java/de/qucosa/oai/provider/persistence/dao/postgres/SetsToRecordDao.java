/**
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class SetsToRecordDao<T extends SetsToRecord> implements Dao<SetsToRecord> {

    private Connection connection;

    @Autowired
    public SetsToRecordDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public SetsToRecordDao() {
        this.connection = null;
    }

    @Override
    public SetsToRecord saveAndSetIdentifier(SetsToRecord object) throws SaveFailed {
        String sql = "INSERT INTO sets_to_records (id_set, id_record) VALUES (?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, object.getIdSet());
            ps.setLong(2, object.getIdRecord());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection saveAndSetIdentifier(Collection objects) throws SaveFailed {
        return null;
    }

    @Override
    public SetsToRecord update(SetsToRecord object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection update(Collection objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection findAll() throws NotFound {
        return null;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection findByPropertyAndValue(String property, String value) throws NotFound {
        return null;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        clause = clause.replace("%s", "?");
        String sql = "SELECT * FROM sets_to_records WHERE " + clause;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, Long.valueOf(values[0]));
            ps.setLong(2, Long.valueOf(values[1]));
            ResultSet resultSet = ps.executeQuery();
            SetsToRecord setsToRecord = new SetsToRecord();

            while (resultSet.next()) {
                setsToRecord.setIdSet(resultSet.getLong("id_set"));
                setsToRecord.setIdRecord(resultSet.getLong("id_record"));
            }

            return (T) setsToRecord;
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        return 0;
    }

    @Override
    public SetsToRecord delete(SetsToRecord object) throws DeleteFailed {
        return null;
    }
}
