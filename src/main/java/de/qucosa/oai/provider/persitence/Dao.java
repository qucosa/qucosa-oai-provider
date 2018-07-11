package de.qucosa.oai.provider.persitence;

import java.util.Collections;

public interface Dao<T> {
    public T save(T object);

    public T save(Collections objects);

    public T update(T object);

    public T update(Collections objects);

    public T findAll();

    public T findById(T value);

    public T findByColumnAndValue(String column, T value);

    public T delete(String column, T value);
}
