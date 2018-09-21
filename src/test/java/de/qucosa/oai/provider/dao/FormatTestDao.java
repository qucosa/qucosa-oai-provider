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
import de.qucosa.oai.provider.persistence.model.Format;
import testdata.TestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FormatTestDao<T extends Format> implements Dao<T> {
    @Override
    public Format saveAndSetIdentifier(Format object) throws SaveFailed {

        if (object.getIdentifier() == null) {
            object.setFormatId(Long.valueOf(1));
            return object;
        }

        if (object.getMdprefix().isEmpty() || object.getMdprefix() == null) {
            throw new SaveFailed("Cannot save format because properties are failed.");
        }

        if (object.getSchemaUrl().isEmpty() || object.getSchemaUrl() == null) {
            throw new SaveFailed("Cannot save format because properties are failed.");
        }

        if (object.getNamespace().isEmpty() || object.getNamespace() == null) {
            throw new SaveFailed("Cannot save format because properties are failed.");
        }

        throw new SaveFailed("Cannot save format.");
    }

    @Override
    public Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed {
        int i = 0;

        for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
            i++;
            Format format = (Format) iterator.next();
            format.setFormatId(Long.valueOf(i));
        }

        return objects;
    }

    @Override
    public Format update(Format object) throws UpdateFailed {
        Format format = object;
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
            throw new UpdateFailed("Cannot find formats.");
        }

        return format;
    }

    @Override
    public Collection<T> update(Collection<T> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<T> findAll() throws NotFound {
        ObjectMapper om = new ObjectMapper();
        List<Format> formats;

        try {
            formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
        } catch (IOException e) {
            throw new NotFound("Cannot find formats.");
        }

        return (Collection<T>) formats;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @Override
    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        ObjectMapper om = new ObjectMapper();
        Collection<Format> formats = new ArrayList<>();

        try {
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(property)) {
                    throw new NotFound("Cannot find " + property + " in formats table.");
                }

                if (node.get(property).asText().equals(value)) {
                    Format format = om.readValue(node.toString(), Format.class);
                    format.setFormatId(Long.valueOf(i));
                    formats.add(format);
                    break;
                }
            }

            return (formats.size() > 0) ? (Collection<T>) formats : null;
        } catch (IOException ignore) { }

        throw new NotFound("Cannot find format.");
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
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);

            for (JsonNode node : jsonNodes) {

                if (node.get("mdprefix").asText().equals(ident)) {
                    del = true;
                    break;
                }
            }

            if (!del) {
                throw new DeleteFailed("Cannot delete format.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot parse formats json.");
        }
    }

    @Override
    public void undoDelete(String ident) throws UndoDeleteFailed {
        ObjectMapper om = new ObjectMapper();
        boolean undoDel = false;

        try {
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);

            for (JsonNode node : jsonNodes) {

                if (node.get("mdprefix").asText().equals(ident)) {
                    undoDel = true;
                    break;

                }
            }

            if (!undoDel) {
                throw new UndoDeleteFailed("Cannot undo delete format.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Cannot parse formats json.");
        }
    }

    @Override
    public void delete(T object) throws DeleteFailed {
    }

    @Override
    public void undoDelete(T object) throws UndoDeleteFailed {

    }
}
