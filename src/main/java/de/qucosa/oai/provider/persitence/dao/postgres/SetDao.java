package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

@Repository
public class SetDao<T> implements Dao<T> {
    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public T save(T object) throws SQLException {
        Set input = (Set) object;

        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING;";
        String finalSql = sql;

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, input.getSetSpec());
        ps.setString(2, input.getSetName());
        ps.setString(3, input.getSetDescription());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating Set failed, no rwos affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating Set failed, no ID obtained.");
            }

            input.setSetId(generatedKeys.getLong(1));
        }

        return (T) input;
    }

    @Override
    public T save(Collections objects) {
        return null;
    }

    @Override
    public T update(T object) {
        return null;
    }

    @Override
    public T update(Collections objects) {
        return null;
    }

    @Override
    public T findAll() {
        return null;
    }

    @Override
    public T findById(T value) {
        return null;
    }

    @Override
    public T findByColumnAndValue(String column, T value) throws SQLException {
        String sql = "SELECT id, setspec,setname, setdescription, delete FROM sets where " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, (String) value);
        ResultSet resultSet = ps.executeQuery();
        Set set = null;

        while (resultSet.next()) {
            set.setSetId(resultSet.getLong("id"));
            set.setSetSpec(resultSet.getString("setspec"));
            set.setSetName(resultSet.getString("setname"));
            set.setSetDescription(resultSet.getString("setdescription"));
            set.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();

        return (T) set;
    }

    @Override
    public T delete(String column, T value) throws SQLException {
        String sql = "UPDATE sets SET deleted = true WHERE " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setBoolean(1, (Boolean) value);
        int deletedRows = ps.executeUpdate();

        if (deletedRows == 0) {
            throw new SQLException("Set mark as deleted failed, no rwos affected.");
        }

        return (T) new Long(deletedRows);
    }
}
