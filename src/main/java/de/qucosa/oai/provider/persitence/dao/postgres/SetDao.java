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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Repository
public class SetDao<Tparam> implements Dao<Set, Tparam> {
    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Set save(Tparam object) throws SQLException {
        Set input = (Set) object;

        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";
        String finalSql = sql;

        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, input.getSetSpec());
        ps.setString(2, input.getSetName());
        ps.setString(3, input.getSetDescription());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating Set failed, no rows affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

            if (!generatedKeys.next()) {
                throw new SQLException("Creating Set failed, no ID obtained.");
            }

            input.setSetId(generatedKeys.getLong("id"));
        }

        ps.close();

        return input;
    }

    @Override
    public List<Set> save(Collection objects) throws SQLException {
        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";
        List<Set> output = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        connection.setAutoCommit(false);

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            Set set = (Set) iterator.next();
            ps.clearParameters();
            ps.setString(1, set.getSetSpec());
            ps.setString(2, set.getSetName());
            ps.setString(3, set.getSetDescription());
            ps.addBatch();
        }

        ps.clearParameters();
        int[] insertRows = ps.executeBatch();

        if (insertRows.length == 0) {
            throw new SQLException("Creating Sets failed, no rows affected.");
        }

        try (ResultSet result = ps.getGeneratedKeys()) {

            if (!result.next()) {
                throw new SQLException("Creating Set failed, no ID obtained.");
            }

            do {
                Set set = new Set();
                set.setSetId(result.getLong(1));
                set.setSetSpec(result.getString("setspec"));
                set.setSetName(result.getString("setname"));
                set.setSetDescription(result.getString("setdescription"));
                set.setDeleted(result.getBoolean("deleted"));
                output.add(set);
            } while(result.next());
        }

        connection.commit();
        ps.close();

        return output;
    }

    @Override
    public Set update(Tparam object) throws SQLException {
        Set input = (Set) object;
        String sql = "UPDATE sets SET setname = ?, setdescription = ? where setspec = ? AND deleted = FALSE";
        PreparedStatement ps = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        ps.setString(1, input.getSetName());
        ps.setString(2, input.getSetDescription());
        ps.setString(3, input.getSetSpec());
        int updateRows = ps.executeUpdate();
        connection.commit();

        if (updateRows == 0) {
            throw new SQLException("Update set is failed, no affected rows.");
        }

        Set set = (Set) findByColumnAndValue("setspec", (Tparam) input.getSetSpec());

        ps.close();

        return set;
    }

    @Override
    public List<Set> update(Collection objects) {
        return null;
    }

    @Override
    public List<Set> findAll() throws SQLException {
        String sql = "SELECT id, setspec, setname, setdescription, deleted FROM sets";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        List<Set> sets = new ArrayList<>();

        if (resultSet.next()) {

            do {
                Set set = new Set();
                set.setSetId(resultSet.getLong(1));
                set.setSetSpec(resultSet.getString("setspec"));
                set.setSetName(resultSet.getString("setname"));
                set.setSetDescription(resultSet.getString("setdescription"));
                set.setDeleted(resultSet.getBoolean("deleted"));
                sets.add(set);
            } while(resultSet.next());
        }

        return sets;
    }

    @Override
    public Set findById(Tparam value) {
        return null;
    }

    @Override
    public Set findByColumnAndValue(String column, Tparam value) throws SQLException {
        String sql = "SELECT id, setspec,setname, setdescription, deleted FROM sets where " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, (String) value);
        ResultSet resultSet = ps.executeQuery();
        Set set = new Set();

        while (resultSet.next()) {
            set.setSetId(resultSet.getLong("id"));
            set.setSetSpec(resultSet.getString("setspec"));
            set.setSetName(resultSet.getString("setname"));
            set.setSetDescription(resultSet.getString("setdescription"));
            set.setDeleted(resultSet.getBoolean("deleted"));
        }

        resultSet.close();
        ps.close();

        return set;
    }

    @Override
    public List<Set> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Set delete(String column, Tparam ident, boolean value) throws SQLException {
        String sql = "UPDATE sets SET deleted = ? WHERE " + column + " = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        ps.setBoolean(1, value);
        ps.setString(2, (String) ident);
        int deletedRows = ps.executeUpdate();
        connection.commit();

        if (deletedRows == 0) {
            throw new SQLException("Set mark as deleted failed, no rwos affected.");
        }

        Set set = (Set) findByColumnAndValue(column, ident);

        ps.close();

        return set;
    }
}
