package de.qucosa.oai.provider.persitence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

public interface Dao<T> {
    public T save(T object) throws SQLException;

    public T save(Collections objects);

    public T update(T object) throws SQLException;

    public T update(Collections objects);

    public T findAll();

    public T findById(T value);

    public T findByColumnAndValue(String column, T value) throws SQLException;

    public T delete(String column, T value) throws SQLException;
}
