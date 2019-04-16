/*
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
import de.qucosa.oai.provider.persistence.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class RecordDao<T extends Record> implements Dao<Record> {

    private Connection connection;

    @Autowired
    public RecordDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public RecordDao() {
        this.connection = null;
    }

    @Override
    public Record saveAndSetIdentifier(Record object) throws SaveFailed {
        String sql = "INSERT INTO records (id, pid, uid) VALUES (nextval('oaiprovider'), ?, ?)";
        sql+="ON CONFLICT (uid) ";
        sql+="DO NOTHING";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, object.getPid());
            ps.setString(2, object.getUid());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SaveFailed("Creating format failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SaveFailed("Creating format failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<Record> saveAndSetIdentifier(Collection<Record> objects) throws SaveFailed {
        return null;
    }

    @Override
    public Record update(Record object) throws UpdateFailed {
        String sql = "UPDATE records SET pid = ?, deleted = ? WHERE uid = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, object.getPid());
            ps.setBoolean(2, object.isDeleted());
            ps.setString(3, object.getUid());
            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new UpdateFailed("Cannot update record.");
            }

            ps.close();
        } catch (SQLException e) {
            throw new UpdateFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<Record> update(Collection<Record> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<Record> findAll() throws NotFound {
        Collection<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records";

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    Record record = new Record();
                    record.setIdentifier(resultSet.getLong("id"));
                    record.setPid(resultSet.getString("pid"));
                    record.setUid(resultSet.getString("uid"));
                    record.setDeleted(resultSet.getBoolean("deleted"));
                    records.add(record);
                } while (resultSet.next());
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }

        return records;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<Record> findByPropertyAndValue(String property, String value) throws NotFound {
        Record record = new Record();
        Collection<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE " + property + " = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();


            while (resultSet.next()) {
                record.setIdentifier(resultSet.getLong("id"));
                record.setPid(resultSet.getString("pid"));
                record.setUid(resultSet.getString("uid"));
                record.setDeleted(resultSet.getBoolean("deleted"));
                records.add(record);
            }

            if (records.size() == 0) {
                return null;
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            throw new NotFound("SQL-ERROR: Cannot found record.", e);
        }

        return records;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<Record> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<Record> findLastRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public Collection<Record> findFirstRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) throws DeleteFailed {
        String sql = "DELETE FROM records WHERE uid = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ident);
            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                throw new DeleteFailed("Cannot delete record.");
            }
        } catch (SQLException e) {
            throw new DeleteFailed("SQL-ERROR: Cannot delete record.", e);
        }
    }

    @Override
    public void delete(Record object) throws DeleteFailed {
    }
}
