package de.qucosa.oai.provider.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.SetsToRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class SetsToRecordTestDao implements Dao {

    @Autowired
    public void setConnection(ComboPooledDataSource dataSource) throws SQLException {
    }

    @Override
    public SetsToRecord save(Object object) throws SQLException {
        SetsToRecord setsToRecord = (SetsToRecord) object;
        return setsToRecord;
    }

    @Override
    public List save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Object update(Object object) throws SQLException {
        return null;
    }

    @Override
    public List update(Collection objects) {
        return null;
    }

    @Override
    public List findAll() throws SQLException {
        return null;
    }

    @Override
    public Object findById(Object value) {
        return null;
    }

    @Override
    public Object findByColumnAndValue(String column, Object value) throws SQLException {
        return null;
    }

    @Override
    public Object findByMultipleValues(String clause, String... values) throws SQLException {
        return 0;
    }

    @Override
    public List findAllByColumnAndValue(String column, Object value) throws SQLException {
        return null;
    }

    @Override
    public Object delete(String column, Object ident, boolean value) throws SQLException {
        return null;
    }

    @Override
    public Object delete(Object object) throws SQLException {
        return null;
    }
}
