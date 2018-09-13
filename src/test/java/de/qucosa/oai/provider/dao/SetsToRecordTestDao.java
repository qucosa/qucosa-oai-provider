package de.qucosa.oai.provider.dao;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SetsToRecordTestDao<T extends SetsToRecord> implements Dao<T> {
    @Override
    public SetsToRecord saveAndSetIdentifier(SetsToRecord object) throws SaveFailed {
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
        return null;
    }

    @Override
    public T findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public int delete(String column, String ident, boolean value) throws DeleteFailed {
        return 0;
    }

    @Override
    public T delete(T object) throws DeleteFailed {
        return null;
    }
}
