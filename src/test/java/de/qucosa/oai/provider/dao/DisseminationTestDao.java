package de.qucosa.oai.provider.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DisseminationTestDao<Tparam> implements Dao<Dissemination, Tparam> {
    @Override
    public Dissemination save(Tparam object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;
        dissemination.setDissId(Long.valueOf(1));
        return dissemination;
    }

    @Override
    public List<Dissemination> save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Dissemination update(Tparam object) throws SQLException {
        return null;
    }

    @Override
    public List<Dissemination> update(Collection objects) {
        return null;
    }

    @Override
    public List<Dissemination> findAll() throws SQLException {
        return null;
    }

    @Override
    public Dissemination findById(Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Dissemination delete(String column, Tparam ident, boolean value) throws SQLException {
        return null;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }
}
