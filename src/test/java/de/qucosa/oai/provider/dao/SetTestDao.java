package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SetTestDao<Tparam> implements Dao<Set, Tparam> {
    @Override
    public Set save(Tparam object) throws SQLException {
        Set set = (Set) object;
        set.setSetId(new Long(1));
        return set;
    }

    @Override
    public List<Set> save(Collection objects) throws SQLException {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Set set = (Set) iterator.next();
            set.setSetId(Long.valueOf(i));
        }

        return (List<Set>) objects;
    }

    @Override
    public Set update(Tparam object) throws SQLException {
        Set set = (Set) object;
        ObjectMapper om = new ObjectMapper();

        try {
            List<Set> sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));

            for (Set iter : sets) {

                if (iter.getSetSpec().equals(set.getSetSpec())) {
                    iter.setSetName(set.getSetName());
                    iter.setSetDescription(set.getSetDescription());
                    set = iter;
                    break;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No sets found.");
        }

        return set;
    }

    @Override
    public List<Set> update(Collection objects) {
        return null;
    }

    @Override
    public List<Set> findAll() throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Set> sets = null;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {
            throw new SQLException("No sets found.");
        }

        return sets;
    }

    @Override
    public Set findById(Tparam value) {
        return null;
    }

    @Override
    public Set findByColumnAndValue(String column, Tparam value) throws SQLException {
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

        return set;
    }

    @Override
    public List<Set> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Set delete(String column, Tparam ident, boolean value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Set set = null;

        try {
            JsonNode nodes = om.readTree(TestData.SETS);

            for (JsonNode entry : nodes) {

                if (!entry.has(column)) {
                    throw new SQLException("Set mark as deleted failed, no rwos affected.");
                }

                if (entry.get(column).asText().equals(ident)) {
                    set = om.readValue(entry.toString(), Set.class);
                    set.setSetId(new Long(1));
                    set.setDeleted(value);
                }
            }
        } catch (IOException e) { }

        return set;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }
}
