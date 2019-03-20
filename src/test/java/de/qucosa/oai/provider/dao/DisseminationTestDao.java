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
import de.qucosa.oai.provider.persistence.model.Dissemination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testdata.TestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DisseminationTestDao<T extends Dissemination> implements Dao<T> {

    private Logger logger = LoggerFactory.getLogger(DisseminationTestDao.class);

    @Override
    public Dissemination saveAndSetIdentifier(Dissemination object) throws SaveFailed {

        if (object.getRecordId() != null && object.getFormatId() != null) {
            object.setDissId(Long.valueOf(1));
            return object;
        }

        throw new SaveFailed("Cannot save dissemination.");
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
                    throw new NotFound("Cannot find " + property + " in disseminations table.");
                }

                if (node.get(property).asText().equals(value)) {
                    Dissemination dissemination;
                    dissemination = om.readValue(node.toString(), Dissemination.class);
                    dissemination.setDissId(Long.valueOf(i));
                    disseminations.add(dissemination);
                }
            }

            return (disseminations.size() > 0) ? (Collection<T>) disseminations : null;
        } catch (IOException ignore) { }

        throw new NotFound("Cannot find dissemination.");
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
            throw new NotFound("Cannot find disseminations.");
        }

        throw new NotFound("Cannot find dissemination.");
    }

    @Override
    public Collection<T> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public void delete(String ident) throws DeleteFailed {

    }

    @Override
    public void undoDelete(String ident) throws UndoDeleteFailed {

    }

    @Override
    public void undoDelete(Dissemination object) throws UndoDeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean undoDel = false;

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);

            for (JsonNode node : nodes) {
                Dissemination dissemination = om.readValue(node.toString(), Dissemination.class);

                if (dissemination.getFormatId().equals(object.getFormatId()) && dissemination.getRecordId().equals(object.getRecordId())) {
                    undoDel = true;
                    break;
                }
            }

            if (!undoDel) {
                throw new UndoDeleteFailed("Cannot undo delete dissemination.");
            }
        } catch (IOException e) {
            logger.error("Cannot parse tree from disseminations json.", e);
        }
    }

    @Override
    public void delete(Dissemination object) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean del = false;

        try {
            JsonNode nodes = om.readTree(TestData.DISSEMINATIONS);

            for (JsonNode node : nodes) {
                Dissemination dissemination = om.readValue(node.toString(), Dissemination.class);

                if (dissemination.getFormatId().equals(object.getFormatId()) && dissemination.getRecordId().equals(object.getRecordId())) {
                    del = true;
                    break;

                }
            }

            if (!del) {
                throw new DeleteFailed("Cannot delete dissemination.");
            }
        } catch (IOException e) {
            logger.error("Cannot parse tree from disseminations json.", e);
        }
    }
}
