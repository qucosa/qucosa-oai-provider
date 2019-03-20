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
import de.qucosa.oai.provider.persistence.model.Record;
import testdata.TestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecordTestDao<T extends Record> implements Dao<T> {
    private List<Record> store = new ArrayList<>();

    @Override
    public Record saveAndSetIdentifier(Record object) throws SaveFailed {
        object.setRecordId(Long.valueOf(1));

        if (object.getIdentifier() == null) {
            throw new SaveFailed("Cannot save record.");
        }

        return object;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        return null;
    }

    @Override
    public Record update(Record object) throws UpdateFailed {
        ObjectMapper om = new ObjectMapper();

        try {
            List<Record> records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));

            for (Record record : records) {

                if (!record.getUid().equals(object.getUid())) {
                    continue;
                }

                record = object;
                return record;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse records.", e);
        }

        throw new UpdateFailed("Cannot update record.");
    }

    @Override
    public Collection<T> update(Collection<T> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        ObjectMapper om = new ObjectMapper();
        List<Record> records = null;

        try {
            records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (Collection<T>) records;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        ObjectMapper om = new ObjectMapper();
        Collection<Record> records = new ArrayList<>();

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(property)) {
                    throw new NotFound("Cannot find " + property + " in records table.");
                }

                if (node.get(property).asText().equals(value)) {
                    Record record = om.readValue(node.toString(), Record.class);
                    record.setRecordId(Long.valueOf(i));
                    records.add(record);
                }
            }

            return (Collection<T>) records;
        } catch (IOException ignore) {

        }

        throw new NotFound("Cannot find record.");
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public void delete(String ident) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean del = false;

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);

            for (JsonNode node : jsonNodes) {

                if (node.get("uid").asText().equals(ident)) {
                    del = true;
                    break;
                }
            }

            if (!del) {
                throw new DeleteFailed("Cannot delete record.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse records json.");
        }
    }

    @Override
    public void undoDelete(String ident) throws UndoDeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean undoDel = false;

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);

            for (JsonNode node : jsonNodes) {

                if (node.get("uid").asText().equals(ident)) {
                    undoDel = true;
                    break;
                }
            }

            if (!undoDel) {
                throw new UndoDeleteFailed("Cannot undo delete record.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse records json.");
        }
    }

    @Override
    public void delete(T object) throws DeleteFailed {
    }

    @Override
    public void undoDelete(T object) throws UndoDeleteFailed {

    }
}
