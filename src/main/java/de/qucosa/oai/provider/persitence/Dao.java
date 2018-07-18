package de.qucosa.oai.provider.persitence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public interface Dao<T> {
    public T save(T object) throws SQLException;

    public T save(Collection objects) throws SQLException;

    public T update(T object) throws SQLException;

    public T update(Collection objects);

    public T findAll() throws SQLException;

    public T findById(T value);

    public T findByColumnAndValue(String column, T value) throws SQLException;

    public T delete(String column, T value) throws SQLException;
}
