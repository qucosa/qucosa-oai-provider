package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.SetsToRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class SetsToRecordDao implements Dao {

    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Object save(Object object) throws SQLException {
        SetsToRecord setsToRecord = (SetsToRecord) object;
        String sql = "INSERT INTO sets_to_records (id_set, id_record) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, setsToRecord.getIdSet());
        ps.setLong(2, setsToRecord.getIdRecord());
        ps.executeUpdate();

        return null;
    }

    @Override
    public List save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Object update(Object object) throws SQLException {
        return null;
    }

    @Override
    public List update(Collection objects) {
        return null;
    }

    @Override
    public List findAll() throws SQLException {
        return null;
    }

    @Override
    public Object findById(Object value) {
        return null;
    }

    @Override
    public Object findByColumnAndValue(String column, Object value) throws SQLException {
        return null;
    }

    @Override
    public Integer findByMultipleValues(String clause, String... values) throws SQLException {
        clause = clause.replace("%s", "?");
        String sql = "SELECT * FROM sets_to_records WHERE " + clause;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, Long.valueOf(values[0]));
        ps.setLong(2, Long.valueOf(values[1]));
        ResultSet resultSet = ps.executeQuery();

        return (resultSet.next()) ? 1 : 0;
    }

    @Override
    public List findAllByColumnAndValue(String column, Object value) throws SQLException {
        return null;
    }

    @Override
    public Object delete(String column, Object ident, boolean value) throws SQLException {
        return null;
    }

    @Override
    public Object delete(Object object) throws SQLException {
        return null;
    }
}
