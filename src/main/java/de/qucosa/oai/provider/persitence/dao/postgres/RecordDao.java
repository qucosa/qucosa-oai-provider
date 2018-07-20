package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class RecordDao<Tparam> implements Dao<Record, Tparam> {

    private Connection connection;

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
        this.connection = dataSource.getConnection();
    }

    @Override
    public Record save(Tparam object) {
        return null;
    }

    @Override
    public List<Record> save(Collection objects) {
        return null;
    }

    @Override
    public Record update(Tparam object) {
        return null;
    }

    @Override
    public List<Record> update(Collection objects) {
        return null;
    }

    @Override
    public List<Record> findAll() {
        return null;
    }

    @Override
    public Record findById(Tparam value) {
        return null;
    }

    @Override
    public Record findByColumnAndValue(String column, Tparam value) {
        return null;
    }

    @Override
    public Record delete(String column, Tparam ident, boolean value) {
        return null;
    }
}
