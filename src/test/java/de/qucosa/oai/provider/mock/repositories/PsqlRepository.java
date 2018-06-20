package de.qucosa.oai.provider.mock.repositories;

import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;

import java.sql.SQLException;
import java.util.Set;

public class PsqlRepository implements PersistenceDaoInterface {

    @Override
    public <T> T create(T object) throws SQLException { return null; }

    @Override
    public int count(String cntField, String... whereClauses) {
        return 0;
    }

    @Override
    public int count(String cntField, String whereColumn, String whereColumnValue) throws SQLException { return 0; }

    @Override
    public <T> Set<T> findAll() {
        return null;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) throws SQLException { return null; }

    @Override
    public <T> T update(String sql) { return null; }

    @Override
    public <T> T update(String... value) { return null; }

    @Override
    public <T> T update(T object) throws SQLException {
        return object;
    }

    @Override
    public <T> T findById(Long id) {
        return null;
    }

    @Override
    public <T> T findByIds(T... values) {
        return null;
    }

    @Override
    public <T> T findByValue(String column, String value) throws SQLException { return null; }

    @Override
    public <T> T findByValues(Set<T> values) {
        return null;
    }

    @Override
    public <T> T findByValues(String... values) {
        return null;
    }

    @Override
    public void deleteById(Long id) { }

    @Override
    public <T> void deleteByKeyValue(String key, T value) throws SQLException { }

    @Override
    public void deleteByKeyValue(String... paires) { }

    @Override
    public <T> void deleteByValues(Set<T> values) { }

    @Override
    public void runProcedure() throws SQLException { }
}
