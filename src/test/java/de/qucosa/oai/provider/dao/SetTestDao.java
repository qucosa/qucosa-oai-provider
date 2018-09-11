package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testdata.TestData;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SetTestDao<T extends Set> implements Dao<T> {

    private Logger logger = LoggerFactory.getLogger(SetTestDao.class);

    @Override
    public T saveAndSetIdentifier(T object) throws SaveFailed {
        Set set = (Set) object;
        set.setIdentifier(new Long(1));
        return (T) set;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Set set = (Set) iterator.next();
            set.setIdentifier(Long.valueOf(i));
        }

        return objects;
    }

    @Override
    public T update(T object) throws UpdateFailed {
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
            throw new UpdateFailed("No sets found.");
        }

        return (T) set;
    }

    @Override
    public Collection<Set> update(Collection objects) {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        ObjectMapper om = new ObjectMapper();
        Collection<Set> sets;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {
            throw new NotFound("No sets found.");
        }

        return (Collection<T>) sets;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        ObjectMapper om = new ObjectMapper();
        Collection<Set> sets = null;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {
            logger.error("Cannot parse sets data objects.", e);
        }

        assert sets != null;

        return (Collection<T>) sets;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();
        int deleted = 0;

        try {
            JsonNode nodes = om.readTree(TestData.SETS);

            for (JsonNode entry : nodes) {

                if (!entry.has(column)) {
                    throw new DeleteFailed("Set mark as deleted failed, no rwos affected.");
                }

                if (entry.get(column).asText().equals(ident)) {
                    deleted = 1;
                }
            }
        } catch (IOException e) {
            logger.error("Cannot parse tree from sets data input objects.", e);
        }

        return deleted;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}
