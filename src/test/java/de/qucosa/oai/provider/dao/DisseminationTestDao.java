package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Dissemination dissemination;

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
        Map<String, Object> psValues = new HashMap<>();
        //noinspection ConfusingArgumentToVarargsMethod
        clause = String.format(clause, values);
        String[] clauseCutOnLogicOperand = clause.split("AND | OR");

        for (int i = 0; i < clauseCutOnLogicOperand.length; i++) {
            String[] cut = clauseCutOnLogicOperand[i].split("=");
            psValues.put(cut[0].trim(), cut[1].trim());
        }

        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);
            int i = 0;

            for (JsonNode node : nodes) {
                i++;
                dissemination = om.readValue(node.toString(), Dissemination.class);

                if (dissemination.getFormatId().equals(Long.valueOf(psValues.get("formatid").toString())) && dissemination.getRecordId().equals(psValues.get("recordid"))) {
                    dissemination.setDissId(Long.valueOf(1));
                    return dissemination;
                }
            }

        } catch (IOException e) {
            throw new SQLException("No disseminations found.");
        }

        throw new SQLException("No dissemination found.");
    }

    @Override
    public List<Dissemination> findAllByColumnAndValue(String column, Tparam value) throws SQLException {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Dissemination delete(Tparam object) throws SQLException {
        Dissemination dissemination = (Dissemination) object;
        dissemination.isDeleted();
        return dissemination;
    }

    @Override
    public void setConnection(ComboPooledDataSource comboPooledDataSource) throws SQLException {

    }
}
