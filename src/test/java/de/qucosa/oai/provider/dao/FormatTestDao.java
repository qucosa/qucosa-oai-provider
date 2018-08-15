package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FormatTestDao<Tparam> implements Dao<Format, Tparam> {
    @Override
    public Format save(Tparam object) throws SQLException {
        Format format = (Format) object;
        format.setFormatId(Long.valueOf(1));
        return format;
    }

    @Override
    public List<Format> save(Collection objects) throws SQLException {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Format format = (Format) iterator.next();
            format.setFormatId(Long.valueOf(i));
        }

        return (List<Format>) objects;
    }

    @Override
    public Format update(Tparam object) throws SQLException {
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

        return format;
    }

    @Override
    public List<Format> update(Collection objects) {
        return null;
    }

    @Override
    public List<Format> findAll() throws SQLException {
        ObjectMapper om = new ObjectMapper();
        List<Format> formats;

        try {
            formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        return formats;
    }

    @Override
    public Format findById(Tparam value) {
        return null;
    }

    @Override
    public Format findByColumnAndValue(String column, Tparam value) throws SQLException {
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
                    return format;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No formats found.");
        }

        throw new SQLException("Format is not found.");
    }

    @Override
    public Format findByMultipleValues(String clause, String... values) throws SQLException {
        return null;
    }

    @Override
    public List<Format> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        return null;
    }

    @Override
    public Format delete(String column, Tparam ident, boolean value) throws SQLException {
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

        return format;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException { }
}
