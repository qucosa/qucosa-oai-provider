package de.qucosa.oai.provider.persitence.dao.postgres;

import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.Record;
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
public class RecordDao<T extends Record> implements Dao<T> {

    private Connection connection;

    @Autowired
    public RecordDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public RecordDao() {
        this.connection = null;
    }

    @Override
    public Record saveAndSetIdentifier(Record object) throws SaveFailed {
        String sql = "INSERT INTO records (id, pid, uid) VALUES (nextval('oaiprovider'), ?, ?)";
        sql+="ON CONFLICT (uid) ";
        sql+="DO NOTHING";

        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, object.getPid());
            ps.setString(2, object.getUid());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SaveFailed("Creating format failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SaveFailed("Creating format failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        return null;
    }

    @Override
    public Record update(Record object) throws UpdateFailed {
        String sql = "UPDATE records SET pid = ?, deleted = ? WHERE uid = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, object.getPid());
            ps.setBoolean(2, object.isDeleted());
            ps.setString(3, object.getUid());
            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new UpdateFailed("Record update failed, no rwos affected.");
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
        return null;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        Record record = new Record();
        Collection<Record> records = new ArrayList<>();
        String sql = "SELECT * FROM records WHERE " + property + " = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                record.setIdentifier(resultSet.getLong("id"));
                record.setPid(resultSet.getString("pid"));
                record.setUid(resultSet.getString("uid"));
                record.setDeleted(resultSet.getBoolean("deleted"));
                records.add(record);
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }

        return (Collection<T>) records;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        String sql = "UPDATE records SET deleted = ? WHERE " + column + " = ?";
        int deletedRows = 0;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, value);
            ps.setString(2, ident);
            deletedRows = ps.executeUpdate();

            if (deletedRows == 0) {
                throw new DeleteFailed("Record mark as deleted failed, no rwos affected.");
            }

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return deletedRows;
    }

    @Override
    public Record delete(Record object) throws DeleteFailed {
        return null;
    }
}
