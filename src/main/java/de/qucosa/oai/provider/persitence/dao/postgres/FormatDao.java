package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
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
import java.util.List;

@Repository
public class FormatDao<Tparam> implements Dao<Format, Tparam> {
    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Format save(Tparam object) throws SQLException {
        Format input = (Format) object;

        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (mdprefix) ";
        sql+="DO NOTHING";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, input.getMdprefix());
        ps.setString(2, input.getSchemaUrl());
        ps.setString(3, input.getNamespace());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating format failed, no rows affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating format failed, no ID obtained.");
            }

            input.setFormatId(generatedKeys.getLong("id"));
        }

        ps.close();

        return input;
    }

    @Override
    public List<Format> save(Collection objects) throws SQLException {
        String sql = "INSERT INTO formats (id, mdprefix, schemaurl, namespace) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (mdprefix) ";
        sql+="DO NOTHING";
        List<Format> output = new ArrayList<>();
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
            throw new SQLException("Creating formats failed, no rows affected.");
        }

        try (ResultSet result = ps.getGeneratedKeys()) {

            if (!result.next()) {
                throw new SQLException("Creating formats failed, no ID obtained.");
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

        return output;
    }

    @Override
    public Format update(Tparam object) throws SQLException {
        Format input = (Format) object;
        String sql = "UPDATE formats SET schemaurl = ?, namespace = ? where mdprefix = ? AND deleted = FALSE";
        PreparedStatement ps = connection.prepareStatement(sql);
        connection.setAutoCommit(false);

        ps.setString(1, input.getSchemaUrl());
        ps.setString(2, input.getNamespace());
        ps.setString(3, input.getMdprefix());
        int updateRows = ps.executeUpdate();
        connection.commit();

        if (updateRows == 0) {
            throw new SQLException("Update format is failed, no affected rows.");
        }

        ps.close();

        return findByColumnAndValue("mdprefix", (Tparam) input.getMdprefix());
    }

    @Override
    public List<Format> update(Collection objects) {
        return null;
    }

    @Override
    public List<Format> findAll() throws SQLException {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        List<Format> formats = new ArrayList<>();

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

        return formats;
    }

    @Override
    public Format findById(Tparam value) {
        return null;
    }


    @Override
    public Format findByColumnAndValue(String column, Tparam value) throws SQLException {
        String sql = "SELECT id, mdprefix, schemaurl, namespace, deleted FROM formats where " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, (String) value);
        ResultSet resultSet = ps.executeQuery();
        Format format = new Format();

        while (resultSet.next()) {
            format.setFormatId(resultSet.getLong("id"));
            format.setMdprefix(resultSet.getString("mdprefix"));
            format.setSchemaUrl(resultSet.getString("schemaurl"));
            format.setNamespace(resultSet.getString("namespace"));
            format.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();
        ps.close();

        return format;
    }

    @Override
    public Format findByMultipleValues(String clause, String... values) throws SQLException {
        return null;
    }

    @Override
    public List<Format> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Format delete(String column, Tparam ident, boolean value) throws SQLException {
        String sql = "UPDATE formats SET deleted = ? WHERE " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        ps.setBoolean(1, value);
        ps.setString(2, (String) ident);
        int deletedRows = ps.executeUpdate();
        connection.commit();

        if (deletedRows == 0) {
            throw new SQLException("Format mark as deleted failed, no rwos affected.");
        }

        ps.close();

        return findByColumnAndValue(column, ident);
    }

    @Override
    public Format delete(Tparam object) throws SQLException {
        return null;
    }
}
