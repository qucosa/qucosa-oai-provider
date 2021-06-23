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
import de.qucosa.oai.provider.persistence.model.Set;
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
import java.util.List;

@Repository
public class SetRepository<T extends Set> implements Dao<Set> {
    private final Logger logger = LoggerFactory.getLogger(SetRepository.class);

    private final Connection connection;

    @Autowired
    public SetRepository(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public SetRepository() {
        connection = null;
    }

    @Override
    public Set saveAndSetIdentifier(Set object) {

        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";

        PreparedStatement ps;

        try {
            assert connection != null;
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, object.getSetSpec());
            ps.setString(2, object.getSetName());
            ps.setString(3, object.getSetDescription());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    AppErrorHandler aeh = new AppErrorHandler(logger)
                            .level(Level.WARN)
                            .message("Cannot save set " + object.getSetSpec());
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
    public Collection<Set> saveAndSetIdentifier(Collection<Set> objects) {
        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";
        List<Set> output = new ArrayList<>();

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);

            for (Set set : objects) {
                ps.clearParameters();
                ps.setString(1, set.getSetSpec());
                ps.setString(2, set.getSetName());
                ps.setString(3, set.getSetDescription());
                ps.addBatch();
            }

            ps.clearParameters();
            ps.executeBatch();

            try (ResultSet result = ps.getGeneratedKeys()) {

                if (!result.next()) {
                    AppErrorHandler aeh = new AppErrorHandler(logger)
                            .level(Level.WARN)
                            .message("Cannot save set.");
                    aeh.log();
                }

                do {
                    output.add(setData(result));
                } while(result.next());
            }

            connection.commit();
            ps.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return output;
    }

    @Override
    public Set update(Set object) {
        String sql = "UPDATE sets SET setname = ?, setdescription = ? where setspec = ? AND deleted = FALSE";

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            ps.setString(1, object.getSetName());
            ps.setString(2, object.getSetDescription());
            ps.setString(3, object.getSetSpec());
            int updateRows = ps.executeUpdate();
            connection.commit();

            if (updateRows == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot update set " + object.getSetSpec());
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
    public Collection<Set> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<Set> findAll() {
        String sql = "SELECT id, setspec, setname, setdescription, deleted FROM sets";
        Collection<Set> sets = new ArrayList<>();

        try {
            assert connection != null;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    sets.add(setData(resultSet));
                } while(resultSet.next());
            } else {
                return sets;
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return sets;
    }

    @Override
    public Set findById(String id) {
        return new Set();
    }

    @Override
    public Collection<Set> findByPropertyAndValue(String property, String value) {
        String sql = "SELECT id, setspec,setname, setdescription, deleted FROM sets where " + property + " = ?";
        Collection<Set> sets = new ArrayList<>();

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                sets.add(setData(resultSet));
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }

        return sets;
    }

    @Override
    public Set findByMultipleValues(String clause, String... values) {
        return new Set();
    }

    @Override
    public Collection<Set> findRowsByMultipleValues(String clause, String... values) {
        return new ArrayList<>();
    }

    @Override
    public Collection<Set> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {

    }

    @Override
    public void delete(Set object) {
        String sql = "DELETE FROM sets WHERE setspec = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, object.getSetSpec());
            int del = statement.executeUpdate();

            if (del == 0) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN)
                        .message("Cannot delete set " + object.getSetSpec());
                aeh.log();
            }
        } catch (SQLException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).exception(e).message(e.getMessage())
                    .level(Level.ERROR);
            aeh.log();
            throw new RuntimeException(e);
        }
    }

    private Set setData(ResultSet resultSet) throws SQLException {
        Set set = new Set();
        set.setIdentifier(resultSet.getLong("id"));
        set.setSetSpec(resultSet.getString("setspec"));
        set.setSetName(resultSet.getString("setname"));
        set.setSetDescription(resultSet.getString("setdescription"));
        set.setDeleted(resultSet.getBoolean("deleted"));
        return set;
    }
}
