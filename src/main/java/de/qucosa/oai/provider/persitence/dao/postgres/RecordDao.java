package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class RecordDao<T> implements Dao<T> {

    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public T save(T object) {
        return null;
    }

    @Override
    public T save(Collection objects) {
        return null;
    }

    @Override
    public T update(T object) {
        return null;
    }

    @Override
    public T update(Collection objects) {
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
    public T findByColumnAndValue(String column, T value) {
        return null;
    }

    @Override
    public T delete(String column, T ident, boolean value) {
        return null;
    }
}
