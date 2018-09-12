package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import testdata.TestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DisseminationTestDao<T extends Dissemination> implements Dao<T> {
    @Override
    public Dissemination saveAndSetIdentifier(Dissemination object) throws SaveFailed {
        object.setDissId(Long.valueOf(1));
        return object;
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        return null;
    }

    @Override
    public T update(T object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> update(Collection<T> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        return null;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        ObjectMapper om = new ObjectMapper();
        Collection<Dissemination> disseminations = new ArrayList<>();


        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);
            int i = 0;

            for (JsonNode node : nodes) {
                i++;

                if (!node.has(property)) {
                    throw new NotFound(property + " not found in disseminations table.");
                }

                if (node.get(property).asText().equals(value)) {
                    Dissemination dissemination;
                    dissemination = om.readValue(node.toString(), Dissemination.class);
                    dissemination.setDissId(Long.valueOf(i));
                    disseminations.add(dissemination);
                }
            }
        } catch (IOException e) {
            throw new NotFound("No disseminations found.");
        }

        return (Collection<T>) disseminations;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
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
                    return (T) dissemination;
                }
            }

        } catch (IOException e) {
            throw new NotFound("No disseminations found.");
        }

        throw new NotFound("No dissemination found.");
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        return 0;
    }

    @Override
    public Dissemination delete(Dissemination object) throws DeleteFailed {
        return object;
    }
}
