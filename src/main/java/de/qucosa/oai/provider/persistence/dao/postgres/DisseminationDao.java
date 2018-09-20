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
import java.util.Collection;

@Repository
public class DisseminationDao<T extends Dissemination> implements Dao<T> {

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

        try {
            selectDiss = this.findByMultipleValues(
                    "id_format=? AND id_record=?",
                    String.valueOf(object.getFormatId()), object.getRecordId());
        } catch (NotFound ignore) { }

        if (selectDiss != null && selectDiss.getDissId() != null) {
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
        } catch (SQLException ignore) { }

        throw new SaveFailed("Cannot save dissemination.");
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        return null;
    }

    @Override
    public T update(T object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> update(Collection<T> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        return null;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        return null;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        clause = clause.replace("%s", "?");

        if (values[0] == null || values[0].isEmpty() || values[1] == null || values[1].isEmpty()) {
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

            return (T) dissemination;
        } catch (SQLException e) {
            throw new NotFound("Connat find dissemination.", e);
        }
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        return 0;
    }

    @Override
    public Dissemination delete(Dissemination object) throws DeleteFailed {
        String sql = "UPDATE disseminations SET deleted = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, object.isDeleted());
            ps.setLong(2, object.getDissId());
            int deletedRows = ps.executeUpdate();

            if (deletedRows == 0) {
                throw new DeleteFailed("Dissemination mark as deleted failed, no rwos affected.");
            }
        } catch (SQLException e) {
            throw new DeleteFailed(e.getMessage());
        }

        return object;
    }
}
