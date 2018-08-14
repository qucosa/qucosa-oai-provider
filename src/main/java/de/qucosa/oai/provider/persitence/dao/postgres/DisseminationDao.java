package de.qucosa.oai.provider.persitence.dao.postgres;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class DisseminationDao<Tparam> implements Dao<Dissemination, Tparam> {
    @Override
    public Dissemination save(Tparam object) {
        return null;
    }

    @Override
    public List<Dissemination> save(Collection objects) {
        return null;
    }

    @Override
    public Dissemination update(Tparam object) {
        return null;
    }

    @Override
    public List<Dissemination> update(Collection objects) {
        return null;
    }

    @Override
    public List<Dissemination> findAll() {
        return null;
    }

    @Override
    public Dissemination findById(Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByColumnAndValue(String column, Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByMultipleValues(String clause, Tparam... values) throws SQLException {
        return null;
    }

    @Override
    public List<Dissemination> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Dissemination delete(String column, Tparam ident, boolean value) {
        return null;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }
}
