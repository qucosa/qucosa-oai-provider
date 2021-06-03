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

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Format;
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
public class FormatDao<T extends Format> implements Dao<Format> {
    private final Connection connection;

    @Autowired
    public FormatDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public FormatDao() {
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
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                //throw new SaveFailed("Cannot save format.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException("Creating format failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();

        } catch (SQLException e) {
            //throw new SaveFailed("Cannot save format.", e);
        }

        return object;
    }

    @Override
    public Collection<Format> saveAndSetIdentifier(Collection<Format> objects) {
        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (mdprefix) ";
        sql+="DO NOTHING";
        Collection<Format> output = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);

            for (Format format : objects) {
                ps.clearParameters();
                ps.setString(1, format.getMdprefix());
                ps.setString(2, format.getSchemaUrl());
                ps.setString(3, format.getNamespace());
                ps.addBatch();
            }

            ps.clearParameters();
            int[] insertRows = ps.executeBatch();

            if (insertRows.length == 0) {
                //throw new SaveFailed("Creating formats failed, no rows affected.");
            }

            try (ResultSet result = ps.getGeneratedKeys()) {

                if (!result.next()) {
                    //throw new SaveFailed("Creating formats failed, no ID obtained.");
                }

                do {
                    output.add(formatData(result));
                } while(result.next());
            }

            connection.commit();
            ps.close();
        } catch (SQLException e) {
            //throw new SaveFailed(e.getMessage());
        }

        return output;
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
                //throw new UpdateFailed("Cannot update format.");
            }

            ps.close();
        } catch (SQLException e) {
            //throw new UpdateFailed(e.getMessage());
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
            //throw new NotFound(e.getMessage());
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
            //throw new NotFound("Format with id (" + id + ") not found.", e);
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
            //throw new NotFound(e.getMessage());
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
                //throw new DeleteFailed("Cannot delete format " + object.getMdprefix() + ".");
            }
        } catch (SQLException e) {
            //throw new DeleteFailed(e.getMessage(), e);
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
