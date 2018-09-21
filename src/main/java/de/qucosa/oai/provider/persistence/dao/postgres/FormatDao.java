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
import java.util.Iterator;

@Repository
public class FormatDao<T extends Format> implements Dao<T> {
    private Connection connection;

    @Autowired
    public FormatDao(Connection connection) throws SQLException {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public FormatDao() {
        this.connection = null;
    }

    @Override
    public Format saveAndSetIdentifier(Format object) throws SaveFailed {
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
                throw new SaveFailed("Cannot save format.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException("Creating format failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();

        } catch (SQLException e) {
            throw new SaveFailed("Cannot save format.", e);
        }

        return object;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (mdprefix) ";
        sql+="DO NOTHING";
        Collection<Format> output = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);

            for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
                Format format = (Format) iterator.next();
                ps.clearParameters();
                ps.setString(1, format.getMdprefix());
                ps.setString(2, format.getSchemaUrl());
                ps.setString(3, format.getNamespace());
                ps.addBatch();
            }

            ps.clearParameters();
            int[] insertRows = ps.executeBatch();

            if (insertRows.length == 0) {
                throw new SaveFailed("Creating formats failed, no rows affected.");
            }

            try (ResultSet result = ps.getGeneratedKeys()) {

                if (!result.next()) {
                    throw new SaveFailed("Creating formats failed, no ID obtained.");
                }

                do {
                    Format format = new Format();
                    format.setFormatId(result.getLong("id"));
                    format.setMdprefix(result.getString("mdprefix"));
                    format.setSchemaUrl(result.getString("schemaurl"));
                    format.setNamespace(result.getString("namespace"));
                    format.setDeleted(result.getBoolean("deleted"));
                    output.add(format);
                } while(result.next());
            }

            connection.commit();
            ps.close();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return (Collection<T>) output;
    }

    @Override
    public Format update(Format object) throws UpdateFailed {
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
                throw new UpdateFailed("Update format is failed, no affected rows.");
            }

            ps.close();
        } catch (SQLException e) {
            throw new UpdateFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<T> update(Collection<T> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats";
        Collection<Format> formats = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    Format format = new Format();
                    format.setFormatId(resultSet.getLong("id"));
                    format.setMdprefix(resultSet.getString("mdprefix"));
                    format.setSchemaUrl(resultSet.getString("schemaurl"));
                    format.setNamespace(resultSet.getString("namespace"));
                    format.setDeleted(resultSet.getBoolean("deleted"));
                    formats.add(format);
                } while (resultSet.next());
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }


        return (Collection<T>) formats;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats where " + property + " = ?";
        Collection<Format> formats = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();
            Format format = new Format();

            while (resultSet.next()) {
                format.setFormatId(resultSet.getLong("id"));
                format.setMdprefix(resultSet.getString("mdprefix"));
                format.setSchemaUrl(resultSet.getString("schemaurl"));
                format.setNamespace(resultSet.getString("namespace"));
                format.setDeleted(resultSet.getBoolean("deleted"));
                formats.add(format);
            }

            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }

        return (Collection<T>) formats;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        String sql = "UPDATE formats SET deleted = ? WHERE " + column + " = ?";
        int deletedRows;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            ps.setBoolean(1, value);
            ps.setString(2, ident);
            deletedRows = ps.executeUpdate();
            connection.commit();

            if (deletedRows == 0) {
                throw new DeleteFailed("Cannot delete format.");
            }

            ps.close();
        } catch (SQLException e) {
            throw new DeleteFailed(e.getMessage());
        }

        return deletedRows;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}
