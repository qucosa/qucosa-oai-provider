package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FormatTestDao<T> implements Dao<T> {
    @Override
    public T save(T object) throws SQLException {
        Format format = (Format) object;
        format.setFormatId(Long.valueOf(1));
        return (T) format;
    }

    @Override
    public T save(Collection objects) throws SQLException {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Format format = (Format) iterator.next();
            format.setFormatId(Long.valueOf(i));
        }

        return (T) objects;
    }

    @Override
    public T update(T object) throws SQLException {
        Format format = (Format) object;
        ObjectMapper om = new ObjectMapper();

        try {
            List<Format> formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));

            for (Format iter : formats) {

                if (iter.getMdprefix().equals(format.getMdprefix())) {
                    iter.setSchemaUrl(format.getSchemaUrl());
                    iter.setNamespace(format.getNamespace());
                    format = iter;
                    break;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        return (T) format;
    }

    @Override
    public T update(Collection objects) {
        return null;
    }

    @Override
    public T findAll() throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Format> formats;

        try {
            formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        return (T) formats;
    }

    @Override
    public T findById(T value) {
        return null;
    }

    @Override
    public T findByColumnAndValue(String column, T value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Format format = null;

        try {
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in formats table.");
                }

                if (node.get(column).asText().equals(value)) {
                    format = om.readValue(node.toString(), Format.class);
                    format.setFormatId(Long.valueOf(i));
                    break;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        return (T) format;
    }

    @Override
    public T delete(String column, T ident, boolean value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Format format = null;

        try {
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in formats table.");
                }

                if (node.get(column).asText().equals(ident)) {
                    format = om.readValue(node.toString(), Format.class);
                    format.setFormatId(Long.valueOf(i));
                    format.setDeleted(value);
                    break;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        return (T) format;
    }
}
