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
package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.AppErrorHandler;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
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
public class DisseminationRepository<T extends Dissemination> implements Dao<Dissemination> {
    private final Logger logger = LoggerFactory.getLogger(DisseminationRepository.class);

    private final Connection connection;

    @Autowired
    public DisseminationRepository(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public DisseminationRepository() {
        this.connection = null;
    }

    @Override
    public Dissemination saveAndSetIdentifier(Dissemination object) {
        Dissemination selectDiss = null;

        if (object.getRecordId() == null || object.getRecordId().isEmpty()
                || object.getFormatId() == null || object.getFormatId() == 0) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR)
                    .message("Cannot save dissemination because record or format failed.");
            aeh.log();
        }

        selectDiss = this.findByMultipleValues(
                "id_record=? AND id_format=?",
                object.getRecordId(), String.valueOf(object.getFormatId()));

        if (selectDiss != null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR)
                    .message("Cannot save dissemination because data row is exists.");
            aeh.log();
            return null;
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
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR)
                            .message("Creating dissemination failed, no ID obtained.");
                    aeh.log();
                    return null;
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return object;
    }

    @Override
    public Collection<Dissemination> saveAndSetIdentifier(Collection<Dissemination> objects) {
        return new ArrayList<>();
    }

    @Override
    public Dissemination update(Dissemination object) {
        String sql = "UPDATE disseminations" +
                " SET id_format = ?, lastmoddate = ?, xmldata = ?, id_record = ?, deleted = ?" +
                " WHERE id_format = ? AND id_record = ?";

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
            statement.setLong(6, object.getFormatId());
            statement.setString(7, object.getRecordId());
            int updatedRows = statement.executeUpdate();
            connection.commit();

            if (updatedRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot update dissemnation for record " + object.getRecordId());
                aeh.log();
                return null;
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return object;
    }

    @Override
    public Collection<Dissemination> update() {
        return null;
    }

    @Override
    public Collection<Dissemination> findAll() {
        return null;
    }

    @Override
    public Dissemination findById(String id) {
        return new Dissemination();
    }

    @Override
    public Collection<Dissemination> findByPropertyAndValue(String property, String value) {
        String sql = "SELECT id, id_format, lastmoddate, xmldata, id_record, deleted FROM disseminations" +
                " WHERE " + property + " = ?";
        Collection<Dissemination> disseminations = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            if (property.equals("id_format")) {
                statement.setLong(1, Long.parseLong(value));
            } else {
                statement.setString(1, value);
            }

            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()) {
                disseminations.add(disseminationData(resultSet));
            }

            resultSet.close();

            //Fixme remove from respository and add check in controller
            if (disseminations.size() == 0) {
                //throw new NotFound("Cannot found dissemination. UID " + value + " does not exists.");
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return disseminations;
    }

    @Override
    public Dissemination findByMultipleValues(String clause, String... values) {

        if (values == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR)
                    .message("Cannot find dissemination because parameters failed.");
            aeh.log();
            return null;
        }

        clause = clause.replace("%s", "?");
        Dissemination dissemination = null;
        String sql = "SELECT id, id_format, lastmoddate, xmldata, id_record, deleted FROM disseminations WHERE " + clause;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            if (clause.contains("id_record") && clause.contains("id_format")) {
                ps.setString(1, values[0]);
                ps.setLong(2, Long.parseLong(values[1]));
            }

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                dissemination = disseminationData(resultSet);
            }


        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return dissemination;
    }

    @Override
    public Collection<Dissemination> findRowsByMultipleValues(String clause, String... values) {
        return new ArrayList<>();
    }

    @Override
    public Collection<Dissemination> findFirstRowsByProperty(String property, int limit) {
        Collection<Dissemination> disseminations = new ArrayList<>();
        String sql = "SELECT * FROM disseminations order by " + property + " ASC";

        if (limit > 0) {
            sql += " limit " + limit;
        }

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                disseminations.add(disseminationData(resultSet));
            }

            resultSet.close();

            //Fixme remove from respository and add check in controller
            if (disseminations.isEmpty()) {
                //throw new NotFound("Not fownd data rows.");
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return disseminations;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) { }

    @Override
    public void delete(Dissemination object) {
        String sql = "DELETE FROM disseminations WHERE id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, object.getDissId());
            int deleteRows = statement.executeUpdate();

            if (deleteRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot delete dissemination.");
                aeh.log();
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).message(e.getMessage())
                    .exception(e);
            aeh.log();
            throw new RuntimeException(e);
        }
    }

    private Dissemination disseminationData(ResultSet resultSet) throws SQLException {
        Dissemination dissemination = new Dissemination();
        dissemination.setDissId(resultSet.getLong("id"));
        dissemination.setFormatId(resultSet.getLong("id_format"));
        dissemination.setRecordId(resultSet.getString("id_record"));
        dissemination.setDeleted(resultSet.getBoolean("deleted"));
        dissemination.setLastmoddate(resultSet.getTimestamp("lastmoddate"));
        dissemination.setXmldata(resultSet.getString("xmldata"));
        return dissemination;
    }
}
