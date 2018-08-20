package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Record;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class RecordTestDao<Tparam> implements Dao<Record, Tparam> {
    @Override
    public Record save(Tparam object) throws SQLException {
        Record record = (Record) object;
        record.setRecordId(Long.valueOf(1));
        return record;
    }

    @Override
    public List<Record> save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Record update(Tparam object) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Record> records;
        Record input = (Record) object;

        try {
            records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));

            for (Record record : records) {

                if (!record.getUid().equals(input.getUid())) {
                    continue;
                }

                record = input;
                return record;
            }
        } catch (IOException e) {
            throw new RuntimeException("Records nor parse.", e);
        }

        throw new SQLException("Record cannot update.");
    }

    @Override
    public List<Record> update(Collection objects) {
        return null;
    }

    @Override
    public List<Record> findAll() throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Record> records = null;

        try {
            records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }

    @Override
    public Record findById(Tparam value) {
        return null;
    }

    @Override
    public Record findByColumnAndValue(String column, Tparam value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Record record;

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in records table.");
                }

                if (node.get(column).asText().equals(value)) {
                    record = om.readValue(node.toString(), Record.class);
                    record.setRecordId(Long.valueOf(i));
                    return record;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return null;
    }

    @Override
    public Record findByMultipleValues(String clause, String... values) throws SQLException {
        return null;
    }

    @Override
    public List<Record> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Record delete(String column, Tparam ident, boolean value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Record record;

        try {
            JsonNode jsonNodes = om.readTree(TestData.RECORDS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in records table.");
                }

                if (node.get(column).asText().equals(ident)) {
                    record = om.readValue(node.toString(), Record.class);
                    record.setRecordId(Long.valueOf(i));
                    record.setDeleted(value);
                    return record;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return null;
    }

    @Override
    public Record delete(Tparam object) throws SQLException {
        return null;
    }
}
