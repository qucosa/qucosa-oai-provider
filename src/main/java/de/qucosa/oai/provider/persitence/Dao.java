package de.qucosa.oai.provider.persitence;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface Dao<Treturn, Tparam> {
    Treturn save(Tparam object) throws SQLException;

    List<Treturn> save(Collection objects) throws SQLException;

    Treturn update(Tparam object) throws SQLException;

    List<Treturn> update(Collection objects);

    List<Treturn> findAll() throws SQLException;

    Treturn findById(Tparam value);

    Treturn findByColumnAndValue(String column, Tparam value) throws SQLException;

    List<Treturn> findAllByColumnAndValue(String column, Tparam value) throws SQLException;

    Treturn delete(String column, Tparam ident, boolean value) throws SQLException;

    void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException;
}
