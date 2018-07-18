package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SetTestDao<T> implements Dao<T> {
    @Override
    public T save(T object) throws SQLException {
        Set set = (Set) object;
        set.setSetId(new Long(1));
        return (T) set;
    }

    @Override
    public T save(Collection objects) throws SQLException {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Set set = (Set) iterator.next();
            set.setSetId(Long.valueOf(i));
        }

        return (T) objects;
    }

    @Override
    public T update(T object) throws SQLException {
        return null;
    }

    @Override
    public T update(Collection objects) {
        return null;
    }

    @Override
    public T findAll() throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Set> sets = null;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {
            throw new SQLException("No sets found.");
        }

        return (T) sets;
    }

    @Override
    public T findById(T value) {
        return null;
    }

    @Override
    public T findByColumnAndValue(String column, T value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Set set = null;
        List<Set> sets = null;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) { }

        for (Set obj : sets) {

            if (obj.getSetSpec().equals(value)) {
                set = obj;
                break;
            }
        }

        return (T) set;
    }

    @Override
    public T delete(String column, T value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Set set = null;

        try {
            JsonNode nodes = om.readTree(TestData.SETS);

            for (JsonNode entry : nodes) {

                if (!entry.has(column)) {
                    throw new SQLException("Set mark as deleted failed, no rwos affected.");
                }

                if (entry.get(column).asText().equals(value)) {
                    set = om.readValue(entry.toString(), Set.class);
                    set.setSetId(new Long(1));
                    set.setDeleted(true);
                }
            }
        } catch (IOException e) { }

        return (T) set.getSetId();
    }
}
