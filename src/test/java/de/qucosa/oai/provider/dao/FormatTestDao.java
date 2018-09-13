package de.qucosa.oai.provider.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.Format;
import testdata.TestData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FormatTestDao<T extends Format> implements Dao<T> {
    @Override
    public Format saveAndSetIdentifier(Format object) throws SaveFailed {
        object.setFormatId(Long.valueOf(1));
        return object;
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
        } catch (IOException e) {
            throw new NotFound("Cannot find formats.");
        }
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        ObjectMapper om = new ObjectMapper();
        int deleted = 0;

        try {
            JsonNode jsonNodes = om.readTree(TestData.FORMATS);
            int i = 0;

            for (JsonNode node : jsonNodes) {
                i++;

                if (!node.has(column)) {
                    throw new RuntimeException("Cannot find " + column + " in formats table.");
                }

                if (node.get(column).asText().equals(ident)) {
                    Format format = om.readValue(node.toString(), Format.class);
                    format.setFormatId(Long.valueOf(i));
                    format.setDeleted(value);

                    if (format.getFormatId() != null) {
                        deleted = 1;
                    }
                }
            }
        } catch (IOException e) {
            throw new DeleteFailed("Cannot delete format.");
        }

        return deleted;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}
