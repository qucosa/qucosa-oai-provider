package de.qucosa.oai.provider.persitence.dao.postgres;

import de.qucosa.oai.provider.persitence.Dao;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class DisseminationDao<T> implements Dao<T> {
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
    public T delete(String column, T value) {
        return null;
    }
}
