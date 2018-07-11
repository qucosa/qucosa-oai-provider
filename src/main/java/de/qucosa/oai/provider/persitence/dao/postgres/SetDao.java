package de.qucosa.oai.provider.persitence.dao.postgres;

import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

@Repository
public class SetDao<T> implements Dao<T> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public T save(T object) throws SQLException {
        Set input = (Set) object;

        String sql = "INSERT INTO sets (id, setspec, setname, setdescription) \n";
        sql+="VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n";
        sql+="ON CONFLICT (setspec) \r\n";
        sql+="DO NOTHING";
        String finalSql = sql;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(finalSql);
            ps.setString(1, input.getSetSpec());
            ps.setString(2, input.getSetName());
            ps.setString(3, input.getSetDescription());
            return ps;
        }, keyHolder);

        input.setSetId((Long) keyHolder.getKey());

        return (T) input;
    }

    @Override
    public T save(Collections objects) {
        return null;
    }

    @Override
    public T update(T object) {
        return null;
    }

    @Override
    public T update(Collections objects) {
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
