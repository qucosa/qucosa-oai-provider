package de.qucosa.oai.provider.mock.repositories;

import de.qucosa.oai.provider.persistence.PersistenceDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class PsqlRepository<T> implements PersistenceDao<T> {

    @Override
    public void setConnection(Connection connection) { }

    @Override
    public T create(T object) throws SQLException { return null; }

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException { return 0; }

    @Override
    public T findAll() {
        return null;
    }

    @Override
    public T find(String sqlStmt) throws SQLException { return null; }

    @Override
    public T update(String sql) { return null; }

    @Override
    public T update(String... value) { return null; }

    @Override
    public T update(T object) throws SQLException { return object; }

    @Override
    public T findById(Long id) {
        return null;
    }

    @Override
    public T findByIds(T... values) {
        return null;
    }

    @Override
    public T findByValue(String column, String value) throws SQLException { return null; }

    @Override
    public T findByValues(Set<T> values) {
        return null;
    }

    @Override
    public T findByValues(String... values) {
        return null;
    }

    @Override
    public void deleteById(Long id) { }

    @Override
    public void deleteByKeyValue(String key, T value) throws SQLException { }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public void deleteByValues(Set<T> values) { }

    @Override
    public void runProcedure() throws SQLException { }
}
