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
package de.qucosa.oai.provider.persistence.repository.postgres;

import de.qucosa.oai.provider.AppErrorHandler;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
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
public class FormatRepository<T extends Format> implements Dao<Format> {
    private Logger logger = LoggerFactory.getLogger(FormatRepository.class);

    private final Connection connection;

    @Autowired
    public FormatRepository(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public FormatRepository() {
        this.connection = null;
    }

    @Override
    public Format saveAndSetIdentifier(Format object) {
        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (mdprefix) ";
        sql+="DO NOTHING";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, object.getMdprefix());
            ps.setString(2, object.getSchemaUrl());
            ps.setString(3, object.getNamespace());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    AppErrorHandler aeh = new AppErrorHandler(logger)
                            .level(Level.WARN)
                            .message("Cannot save format " + object.getMdprefix());
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
    public Collection<Format> saveAndSetIdentifier(Collection<Format> objects) {
        return new ArrayList<>();
    }

    @Override
    public Format update(Format object) {
        String sql = "UPDATE formats SET schemaurl = ?, namespace = ? where mdprefix = ? AND deleted = FALSE";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            ps.setString(1, object.getSchemaUrl());
            ps.setString(2, object.getNamespace());
            ps.setString(3, object.getMdprefix());
            int updateRows = ps.executeUpdate();
            connection.commit();

            if (updateRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot update format " + object.getMdprefix());
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
    public Collection<Format> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<Format> findAll() {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats";
        Collection<Format> formats = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    formats.add(formatData(resultSet));
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

        return formats;
    }

    @Override
    public Format findById(String id) {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats where id = ?";
        Format format = new Format();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, Long.parseLong(id));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                format = formatData(resultSet);
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).message(e.getMessage())
                    .exception(e);
            aeh.log();
            throw new RuntimeException(e);
        }

        return format;
    }

    @Override
    public Collection<Format> findByPropertyAndValue(String property, String value) {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats where " + property + " = ?";
        Collection<Format> formats = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                formats.add(formatData(resultSet));
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).message(e.getMessage())
                    .exception(e);
            aeh.log();
            throw new RuntimeException(e);
        }

        return formats;
    }

    @Override
    public Format findByMultipleValues(String clause, String... values) {
        return new Format();
    }

    @Override
    public Collection<Format> findRowsByMultipleValues(String clause, String... values) {
        return new ArrayList<>();
    }

    @Override
    public Collection<Format> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {

    }

    @Override
    public void delete(Format object) {
        String sql = "DELETE FROM formats where mdprefix = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, object.getMdprefix());
            int deleteRows = statement.executeUpdate();

            if (deleteRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot delete format " + object.getMdprefix());
                aeh.log();
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).message(e.getMessage())
                    .exception(e);
            aeh.log();
            throw new RuntimeException(e);
        }
    }

    private Format formatData(ResultSet resultSet) throws SQLException {
        Format format = new Format();
        format.setFormatId(resultSet.getLong("id"));
        format.setMdprefix(resultSet.getString("mdprefix"));
        format.setSchemaUrl(resultSet.getString("schemaurl"));
        format.setNamespace(resultSet.getString("namespace"));
        format.setDeleted(resultSet.getBoolean("deleted"));
        return  format;
    }
}
