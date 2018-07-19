package de.qucosa.oai.provider.dao;

import de.qucosa.oai.provider.persitence.Dao;

import java.sql.SQLException;
import java.util.Collection;

public class RecordTestDao<T> implements Dao<T> {
    @Override
    public T save(T object) throws SQLException {
        return null;
    }

    @Override
    public T save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public T update(T object) throws SQLException {
        return null;
    }

    @Override
    public T update(Collection objects) {
        return null;
    }

    @Override
    public T findAll() throws SQLException {
        return null;
    }

    @Override
    public T findById(T value) {
        return null;
    }

    @Override
    public T findByColumnAndValue(String column, T value) throws SQLException {
        return null;
    }

    @Override
    public T delete(String column, T ident, boolean value) throws SQLException {
        return null;
    }
}
