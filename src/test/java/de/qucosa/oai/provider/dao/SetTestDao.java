/**
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
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

        if (object.getIdentifier() == null) {
            object.setIdentifier(new Long(1));
            return object;
        }

        throw new SaveFailed("Cannot save set objects.");
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
    public Set update(Set object) throws UpdateFailed {
        ObjectMapper om = new ObjectMapper();

        try {
            List<Set> sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));

            for (Set iter : sets) {

                if (iter.getSetSpec().equals(object.getSetSpec())) {
                    iter.setSetName(object.getSetName());
                    iter.setSetDescription(object.getSetDescription());
                    object = iter;
                    return object;
                }
            }
        } catch (IOException e) {
            throw new UpdateFailed("Cannot find sets.");
        }

        throw new UpdateFailed("Cannot update set.");
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
            throw new NotFound("Cannot find sets.");
        }

        if (sets.size() == 0) {
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
        boolean find = false;

        try {
            sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {
            logger.error("Cannot parse sets data objects.", e);
        }

        assert sets != null;

        for (Set set : sets) {

            if (set.getSetSpec().equals(value)) {
                find = true;
                break;
            }
        }

        if (!find) {
            throw new NotFound("Cannot found set.");
        }

        return (Collection<T>) sets;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public void delete(String ident) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean del = false;

        try {
            JsonNode nodes = om.readTree(TestData.SETS);

            for (JsonNode entry : nodes) {

                if (entry.get("setspec").asText().equals(ident)) {
                    del = true;
                    break;
                }
            }

            if (del == false) {
                throw new DeleteFailed("Cannot delete set.");
            }
        } catch (IOException e) {
            logger.error("Cannot parse tree from sets data input objects.", e);
        }
    }

    @Override
    public void undoDelete(String ident) throws UndoDeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean undoDel = false;

        try {
            JsonNode nodes = om.readTree(TestData.SETS);

            for (JsonNode entry : nodes) {

                if (entry.get("setspec").asText().equals(ident)) {
                    undoDel = true;
                    break;
                }
            }

            if (undoDel == false) {
                throw new UndoDeleteFailed("Cannot undo delete set.");
            }
        } catch (IOException e) {
            logger.error("Cannot parse tree from sets data input objects.", e);
        }
    }

    @Override
    public void delete(T object) throws DeleteFailed {
    }

    @Override
    public void undoDelete(T object) throws UndoDeleteFailed {

    }
}
