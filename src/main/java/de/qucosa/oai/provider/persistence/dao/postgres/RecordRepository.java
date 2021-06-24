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
import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.datatype.DatatypeConfigurationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class RecordRepository<T extends Record> implements Dao<Record> {
    private final Logger logger = LoggerFactory.getLogger(RecordRepository.class);

    private final Connection connection;

    @Autowired
    public RecordRepository(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public RecordRepository() {
        this.connection = null;
    }

    @Override
    public Record saveAndSetIdentifier(Record object) {
        String sql = "INSERT INTO records (id, oaiid, pid, visible) VALUES (nextval('oaiprovider'), ?, ?, ?)";
        sql+="ON CONFLICT (oaiid) ";
        sql+="DO NOTHING";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, object.getOaiid());
            ps.setString(2, object.getPid());
            ps.setBoolean(3, object.isVisible());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    AppErrorHandler aeh = new AppErrorHandler(logger)
                            .level(Level.WARN)
                            .message("Cannot save record " + object.getOaiid());
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
    public Collection<Record> saveAndSetIdentifier(Collection<Record> objects) {
        return new ArrayList<>();
    }

    @Override
    public Record update(Record object) {
        String sql = "UPDATE records SET deleted = ? WHERE oaiid = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, object.isDeleted());
            ps.setString(2, object.getOaiid());
            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot update record " + object.getOaiid());
                aeh.log();
                return null;
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
    public Collection<Record> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<Record> findAll() {
        Collection<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE visible = true";

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    Record record = new Record();
                    record.setIdentifier(resultSet.getLong("id"));
                    record.setDeleted(resultSet.getBoolean("deleted"));
                    records.add(record);
                } while (resultSet.next());
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return records;
    }

    @Override
    public Record findById(String id) {
        return new Record();
    }

    @Override
    public Collection<Record> findByPropertyAndValue(String property, String value) {
        Record record = new Record();
        Collection<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE " + property + " = ? AND visible = true";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();


            while (resultSet.next()) {
                record.setIdentifier(resultSet.getLong("id"));
                record.setPid(resultSet.getString("pid"));
                record.setOaiid(resultSet.getString("oaiid"));
                record.setVisible(resultSet.getBoolean("visible"));
                record.setDeleted(resultSet.getBoolean("deleted"));
                records.add(record);
            }

            if (records.size() == 0) {
                return null;
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return records;
    }

    @Override
    public Record findByMultipleValues(String clause, String... values) {
        return new Record();
    }

    @Override
    public Collection<Record> findRowsByMultipleValues(String clause, String... values) {

        if (values.length == 0 || values.length > 3) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .message("The values parameter may only has one to three values.");
            aeh.log();
        }

        Collection<Record> records = new ArrayList<>();

        String sql = "SELECT rc.id, rc.oaiid, rc.pid, rc.deleted, rc.visible, diss.lastmoddate FROM records rc" +
                " LEFT JOIN disseminations diss ON diss.id_record = rc.oaiid" +
                " WHERE diss.id_format = ? AND rc.visible = true";

        if (clause.isEmpty()) {
            sql += " AND lastmoddate BETWEEN ? AND (?::date + '24 hours'::interval)";
        } else {
            sql += " AND " + clause;
        }

        sql += " ORDER BY diss.lastmoddate ASC";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, Long.parseLong(values[0]));

            if (values.length > 1) {

                if (values.length == 3) {
                    pst.setTimestamp(2, DateTimeConverter.timestampWithTimezone(values[1]));
                    pst.setTimestamp(3, DateTimeConverter.timestampWithTimezone(values[2]));
                } else {
                    pst.setTimestamp(2, DateTimeConverter.timestampWithTimezone(values[1]));
                }
            }

            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                Record record = new Record();
                record.setRecordId(resultSet.getLong("id"));
                record.setDeleted(resultSet.getBoolean("deleted"));
                records.add(record);
            }

            resultSet.close();
            pst.close();
        } catch (SQLException | DatatypeConfigurationException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return records;
    }

    @Override
    public Collection<Record> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {
        String sql = "DELETE FROM records WHERE oaiid = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, ident);
            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot delete record " + ident);
                aeh.log();
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Record object) {
    }
}
