package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import org.apache.commons.lang3.StringUtils;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisseminationTestDao<Tparam> implements Dao<Dissemination, Tparam> {
    @Override
    public Dissemination save(Tparam object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;
        dissemination.setDissId(Long.valueOf(1));
        return dissemination;
    }

    @Override
    public List<Dissemination> save(Collection objects) throws SQLException {
        return null;
    }

    @Override
    public Dissemination update(Tparam object) throws SQLException {
        return null;
    }

    @Override
    public List<Dissemination> update(Collection objects) {
        return null;
    }

    @Override
    public List<Dissemination> findAll() throws SQLException {
        return null;
    }

    @Override
    public Dissemination findById(Tparam value) {
        return null;
    }

    @Override
    public Dissemination findByColumnAndValue(String column, Tparam value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = null;

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);
            int i = 0;

            for (JsonNode node : nodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in disseminations table.");
                }

                if (node.get(column).asText().equals(value)) {
                    dissemination = om.readValue(node.toString(), Dissemination.class);
                    dissemination.setDissId(Long.valueOf(i));
                    return dissemination;
                }
            }
        } catch (IOException e) {
            throw new SQLException("No disseminations found.");
        }

        return null;
    }

    @Override
    public Dissemination findByMultipleValues(String clause, String... values) throws SQLException {
        String result = StringUtils.replaceEachRepeatedly(clause, new String[] {"?", "?"}, values);
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = null;

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);
            int i = 0;

            for (JsonNode node : nodes) {
                i++;


            }

        } catch (IOException e) {
            throw new SQLException("No disseminations found.");
        }

        return dissemination;
    }

    @Override
    public List<Dissemination> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = null;
        List<Dissemination> disseminations = new ArrayList<>();

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);
            int i = 0;

            for (JsonNode node : nodes) {
                i++;

                if (!node.has(column)) {
                    throw new SQLException(column + " not found in disseminations table.");
                }

                if (node.get(column).asText().equals(value)) {
                    dissemination = om.readValue(node.toString(), Dissemination.class);
                    dissemination.setDissId(Long.valueOf(i));
                    disseminations.add(dissemination);
                }
            }
        } catch (IOException e) {
            throw new SQLException("No disseminations found.");
        }

        return disseminations;
    }

    @Override
    public Dissemination delete(String column, Tparam ident, boolean value) throws SQLException {
        return null;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }
}
