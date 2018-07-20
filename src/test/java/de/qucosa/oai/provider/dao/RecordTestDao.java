package de.qucosa.oai.provider.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Record;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class RecordTestDao<Tparam> implements Dao<Record, Tparam> {
    @Override
    public Record save(Tparam object) throws SQLException {
        return null;
    }

    @Override
    public List<Record> save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Record update(Tparam object) throws SQLException {
        return null;
    }

    @Override
    public List<Record> update(Collection objects) {
        return null;
    }

    @Override
    public List<Record> findAll() throws SQLException {
        return null;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }

    @Override
    public Record findById(Tparam value) {
        return null;
    }

    @Override
    public Record findByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Record delete(String column, Tparam ident, boolean value) throws SQLException {
        return null;
    }
}
