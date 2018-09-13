package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.Record;
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
            throw new RuntimeException("Records nor parse.", e);
        }

        throw new UpdateFailed("Record cannot update.");
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
                    throw new NotFound(property + " not found in records table.");
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

        throw new NotFound("Cannot found record data row.");
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new DeleteFailed(column + " not found in records table.");
                }

                if (node.get(column).asText().equals(ident)) {
                    return 1;
                }
            }
        } catch (IOException e) {
            return 0;
        }

        return 0;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}
