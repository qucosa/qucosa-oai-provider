package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
public class RecordDao<Tparam> implements Dao<Record, Tparam> {

    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Record save(Tparam object) throws SQLException {
        Record record = (Record) object;
        String sql = "INSERT INTO records (id, pid, uid) VALUES (nextval('oaiprovider'), ?, ?)";
        sql+="ON CONFLICT (uid) ";
        sql+="DO NOTHING";

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, record.getPid());
        ps.setString(2, record.getUid());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating format failed, no rows affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating format failed, no ID obtained.");
            }

            record.setRecordId(generatedKeys.getLong("id"));
        }

        ps.close();

        return record;
    }

    @Override
    public List<Record> save(Collection objects) {
        return null;
    }

    @Override
    public Record update(Tparam object) throws SQLException {
        Record record = (Record) object;
        String sql = "UPDATE records SET pid = ?, deleted = ? WHERE uid = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, record.getPid());
        ps.setBoolean(2, record.isDeleted());
        ps.setString(3, record.getUid());
        int updatedRows = ps.executeUpdate();

        if (updatedRows == 0) {
            throw new SQLException("Record update failed, no rwos affected.");
        }

        ps.close();

        return this.findByColumnAndValue("uid", (Tparam) record.getUid());
    }

    @Override
    public List<Record> update(Collection objects) {
        return null;
    }

    @Override
    public List<Record> findAll() {
        return null;
    }

    @Override
    public Record findById(Tparam value) {
        return null;
    }

    @Override
    public Record findByColumnAndValue(String column, Tparam value) throws SQLException {
        Record record = new Record();
        String sql = "SELECT * FROM records WHERE " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, (String) value);
        ResultSet resultSet = ps.executeQuery();

        while (resultSet.next()) {
            record.setRecordId(resultSet.getLong("id"));
            record.setPid(resultSet.getString("pid"));
            record.setUid(resultSet.getString("uid"));
            record.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();
        ps.close();

        return record;
    }

    @Override
    public Record findByMultipleValues(String clause, String... values) throws SQLException {
        return null;
    }

    @Override
    public List<Record> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Record delete(String column, Tparam ident, boolean value) throws SQLException {
        String sql = "UPDATE records SET deleted = ? WHERE " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, value);
        ps.setString(2, (String) ident);
        int deletedRows = ps.executeUpdate();

        if (deletedRows == 0) {
            throw new SQLException("Record mark as deleted failed, no rwos affected.");
        }

        Record record = findByColumnAndValue(column, ident);
        ps.close();

        return record;
    }
}
