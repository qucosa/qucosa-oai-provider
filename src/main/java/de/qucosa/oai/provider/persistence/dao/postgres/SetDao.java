package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
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
public class SetDao<T extends Set> implements Dao<T> {

    final private Connection connection;

    @Autowired
    public SetDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public SetDao() {
        connection = null;
    }

    @Override
    public Set saveAndSetIdentifier(Set object) throws SaveFailed {

        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";
        String finalSql = sql;

        PreparedStatement ps;

        try {
            assert connection != null;
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, object.getSetSpec());
            ps.setString(2, object.getSetName());
            ps.setString(3, object.getSetDescription());
            int affectedRows = ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException("Creating Set failed, no ID obtained.");
                }

                object.setIdentifier(generatedKeys.getLong("id"));
            }

            ps.close();
        } catch (SQLException e) {
            throw new SaveFailed("Creating Set failed, no ID obtained.", e);
        }

        return object;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) ";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) ";
        sql+="ON CONFLICT (setspec) ";
        sql+="DO NOTHING";
        List<Set> output = new ArrayList<>();

        try {
            assert connection != null;
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
                throw new SaveFailed("Creating Sets failed, no rows affected.");
            }

            try (ResultSet result = ps.getGeneratedKeys()) {

                if (!result.next()) {
                    throw new SaveFailed("Creating Set failed, no ID obtained.");
                }

                do {
                    Set set = new Set();
                    set.setIdentifier(result.getLong(1));
                    set.setSetSpec(result.getString("setspec"));
                    set.setSetName(result.getString("setname"));
                    set.setSetDescription(result.getString("setdescription"));
                    set.setDeleted(result.getBoolean("deleted"));
                    output.add(set);
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
    public Set update(Set object) throws UpdateFailed {
        String sql = "UPDATE sets SET setname = ?, setdescription = ? where setspec = ? AND deleted = FALSE";

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            ps.setString(1, object.getSetName());
            ps.setString(2, object.getSetDescription());
            ps.setString(3, object.getSetSpec());
            int updateRows = ps.executeUpdate();
            connection.commit();

            if (updateRows == 0) {
                throw new UpdateFailed("Update set is failed, no affected rows.");
            }

            ps.close();
        } catch (SQLException e) {
            throw new UpdateFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<T> update(Collection<T> objects) {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        String sql = "SELECT id, setspec, setname, setdescription, deleted FROM sets";
        Collection<Set> sets = new ArrayList<>();

        try {
            assert connection != null;
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {

                do {
                    Set set = new Set();
                    set.setIdentifier(resultSet.getLong(1));
                    set.setSetSpec(resultSet.getString("setspec"));
                    set.setSetName(resultSet.getString("setname"));
                    set.setSetDescription(resultSet.getString("setdescription"));
                    set.setDeleted(resultSet.getBoolean("deleted"));
                    sets.add(set);
                } while(resultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (Collection<T>) sets;
    }

    @Override
    public T findById(String id) {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        String sql = "SELECT id, setspec,setname, setdescription, deleted FROM sets where " + property + " = ?";
        Collection<Set> sets = new ArrayList<>();

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, value);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Set set = new Set();
                set.setIdentifier(resultSet.getLong("id"));
                set.setSetSpec(resultSet.getString("setspec"));
                set.setSetName(resultSet.getString("setname"));
                set.setSetDescription(resultSet.getString("setdescription"));
                set.setDeleted(resultSet.getBoolean("deleted"));
                sets.add(set);
            }

            resultSet.close();
            ps.close();
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }

        return (Collection<T>) sets;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        String sql = "UPDATE sets SET deleted = ? WHERE " + column + " = ?";
        int deletedRows = 0;

        try {
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            connection.setAutoCommit(false);
            ps.setBoolean(1, value);
            ps.setString(2, ident);
            deletedRows = ps.executeUpdate();
            connection.commit();

            if (deletedRows == 0) {
                throw new DeleteFailed("Set mark as deleted failed, no rwos affected.");
            }

            ps.close();
        } catch (SQLException e) {
            throw new DeleteFailed("Set mark as deleted failed, no rwos affected.", e);
        }

        return deletedRows;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}