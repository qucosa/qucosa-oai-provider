package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
public class DisseminationDao<Tparam> implements Dao<Dissemination, Tparam> {

    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Dissemination save(Tparam object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;

        Dissemination selectDiss = this.findByMultipleValues(
                "id_format=? AND id_record=?",
                String.valueOf(dissemination.getFormatId()), dissemination.getRecordId());

        if (selectDiss.getDissId() != null) {
            return null;
        }

        String sql = "INSERT INTO disseminations (id, id_format, lastmoddate, xmldata, id_record) VALUES " +
                "(nextval('oaiprovider'), ?, ?, ?, ?)";
        SQLXML sqlxml = connection.createSQLXML();
        sqlxml.setString(dissemination.getXmldata());

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, dissemination.getFormatId());
        ps.setTimestamp(2, dissemination.getLastmoddate());
        ps.setSQLXML(3, sqlxml);
        ps.setString(4, dissemination.getRecordId());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating dissemination failed, no rows affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating dissemination failed, no ID obtained.");
            }

            dissemination.setDissId(generatedKeys.getLong("id"));
        }

        ps.close();

        return dissemination;
    }

    @Override
    public List<Dissemination> save(Collection objects) {
        return null;
    }

    @Override
    public Dissemination update(Tparam object) {
        return null;
    }

    @Override
    public List<Dissemination> update(Collection objects) {
        return null;
    }

    @Override
    public List<Dissemination> findAll() {
        return null;
    }

    @Override
    public Dissemination findById(Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByColumnAndValue(String column, Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByMultipleValues(String clause, String... values) throws SQLException {
        clause = clause.replace("%s", "?");
        String sql = "SELECT id, id_format, lastmoddate, xmldata, id_record, deleted FROM disseminations WHERE " + clause;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, Long.valueOf(values[0]));
        ps.setString(2, values[1]);
        ResultSet resultSet = ps.executeQuery();
        Dissemination dissemination = new Dissemination();

        while (resultSet.next()) {
            dissemination.setDissId(resultSet.getLong("id"));
            dissemination.setFormatId(resultSet.getLong("id_format"));
            dissemination.setRecordId(resultSet.getString("id_record"));
            dissemination.setDeleted(resultSet.getBoolean("deleted"));
            dissemination.setLastmoddate(resultSet.getTimestamp("lastmoddate"));
            dissemination.setXmldata(resultSet.getString("xmldata"));
        }

        return dissemination;
    }

    @Override
    public List<Dissemination> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Dissemination delete(String column, Tparam ident, boolean value) {
        return null;
    }

    @Override
    public Dissemination delete(Tparam object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;
        String sql = "UPDATE disseminations SET deleted = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, dissemination.isDeleted());
        ps.setLong(2, dissemination.getDissId());
        int deletedRows = ps.executeUpdate();

        if (deletedRows == 0) {
            throw new SQLException("Dissemination mark as deleted failed, no rwos affected.");
        }

        return dissemination;
    }
}
