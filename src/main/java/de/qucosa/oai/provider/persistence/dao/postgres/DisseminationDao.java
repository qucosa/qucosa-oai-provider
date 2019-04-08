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
import de.qucosa.oai.provider.persistence.model.Dissemination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class DisseminationDao<T extends Dissemination> implements Dao<Dissemination> {

    private Connection connection;

    @Autowired
    public DisseminationDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public DisseminationDao() {
        this.connection = null;
    }

    @Override
    public Dissemination saveAndSetIdentifier(Dissemination object) throws SaveFailed {
        Dissemination selectDiss = null;

        if (object.getRecordId() == null || object.getRecordId().isEmpty()
                || object.getFormatId() == null || object.getFormatId() == 0) {
            throw new SaveFailed("Cannot save dissemination because record or format failed.");
        }

        try {
            selectDiss = this.findByMultipleValues(
                    "id_format=? AND id_record=?",
                    String.valueOf(object.getFormatId()), object.getRecordId());
        } catch (NotFound ignore) { }

        if (selectDiss != null) {
            throw new SaveFailed("Cannot save dissemination because data row is exists.");
        }

        String sql = "INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, id_record) VALUES " +
                "(nextval('oaiprovider'), ?, ?, ?, ?)";

        try {
            SQLXML sqlxml = connection.createSQLXML();
            sqlxml.setString(object.getXmldata());

            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, object.getFormatId());
            ps.setTimestamp(2, object.getLastmoddate());
            ps.setSQLXML(3, sqlxml);
            ps.setString(4, object.getRecordId());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SaveFailed("Cannot save dissemination.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SaveFailed("Creating dissemination failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();
            return object;
        } catch (SQLException e) {
            throw new SaveFailed("SQL Error", e);
        }
    }

    @Override
    public Collection<Dissemination> saveAndSetIdentifier(Collection<Dissemination> objects) throws SaveFailed {
        return null;
    }

    @Override
    public Dissemination update(Dissemination object) throws UpdateFailed {
        String sql = "UPDATE disseminations" +
                " SET id_format = ?, lastmoddate = ?, xmldata = ?, id_record = ?, deleted = ?" +
                " WHERE id = ?";

        try {
            SQLXML sqlxml = connection.createSQLXML();
            sqlxml.setString(object.getXmldata());

            PreparedStatement statement = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            statement.setLong(1, object.getFormatId());
            statement.setTimestamp(2, object.getLastmoddate());
            statement.setSQLXML(3, sqlxml);
            statement.setString(4, object.getRecordId());
            statement.setBoolean(5, object.isDeleted());
            statement.setLong(6, object.getDissId());
            int updatedRows = statement.executeUpdate();
            connection.commit();

            if (updatedRows == 0) {
                throw new UpdateFailed("Cannot update dissemination.");
            }
        } catch (SQLException e) {
            throw new UpdateFailed("SQL-ERROR: Cannot update dissemination.", e);
        }

        return object;
    }

    @Override
    public Collection<Dissemination> update(Collection<Dissemination> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<Dissemination> findAll() throws NotFound {
        return null;
    }

    @Override
    public Dissemination findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<Dissemination> findByPropertyAndValue(String property, String value) throws NotFound {
        String sql = "SELECT id, id_format, lastmoddate, xmldata, id_record, deleted FROM disseminations" +
                " WHERE " + property + " = ?";
        Collection<Dissemination> disseminations = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, value);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new NotFound("Cannot found dissemination. UID " + value + " does not exists.");
            }

            while (resultSet.next()) {
                Dissemination dissemination = new Dissemination();
                dissemination.setDissId(resultSet.getLong("id"));
                dissemination.setFormatId(resultSet.getLong("id_format"));
                dissemination.setRecordId(resultSet.getString("id_record"));
                dissemination.setDeleted(resultSet.getBoolean("deleted"));
                dissemination.setXmldata(resultSet.getString("xmldata"));
                disseminations.add(dissemination);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new NotFound(e.getMessage(), e);
        }

        return disseminations;
    }

    @Override
    public Dissemination findByMultipleValues(String clause, String... values) throws NotFound {
        clause = clause.replace("%s", "?");

        if (values[0] == null || Long.valueOf(values[0]) == 0 || values[1] == null || values[1].isEmpty()) {
            throw new NotFound("Cannot find dissemination becaue record_id or format_id failed.");
        }

        String sql = "SELECT id, id_format, lastmoddate, xmldata, id_record, deleted FROM disseminations WHERE " + clause;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, Long.valueOf(values[0]));
            ps.setString(2, values[1]);
            ResultSet resultSet = ps.executeQuery();
            Dissemination dissemination = null;

            while (resultSet.next()) {
                dissemination = new Dissemination();
                dissemination.setDissId(resultSet.getLong("id"));
                dissemination.setFormatId(resultSet.getLong("id_format"));
                dissemination.setRecordId(resultSet.getString("id_record"));
                dissemination.setDeleted(resultSet.getBoolean("deleted"));
                dissemination.setLastmoddate(resultSet.getTimestamp("lastmoddate"));
                dissemination.setXmldata(resultSet.getString("xmldata"));
            }

            return dissemination;
        } catch (SQLException e) {
            throw new NotFound("Connat find dissemination.", e);
        }
    }

    @Override
    public Collection<Dissemination> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) throws DeleteFailed { }

    @Override
    public void delete(Dissemination object) throws DeleteFailed {
        String sql = "DELETE FROM disseminations WHERE id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, object.getDissId());
            int deleteRows = statement.executeUpdate();

            if (deleteRows == 0) {
                throw new DeleteFailed("Cannot delete dissemination.");
            }
        } catch (SQLException e) {
            throw new DeleteFailed("SQL-ERROR: Cannot delete dissemination.", e);
        }
    }
}
