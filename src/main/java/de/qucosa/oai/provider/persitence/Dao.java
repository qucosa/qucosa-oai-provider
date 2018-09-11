package de.qucosa.oai.provider.persitence;

import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persitence.model.HasIdentifier;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface Dao<T extends HasIdentifier> {

    T saveAndSetIdentifier(T object) throws SaveFailed;

    Collection<T> saveAndSetIdentifier(Collection<T> objects) throws SaveFailed;

    T update(T object) throws UpdateFailed;

    Collection<T> update(Collection<T> objects) throws UpdateFailed;

    Collection<T> findAll() throws NotFound;

    T findById(String id) throws NotFound;

    Collection<T> findByPropertyAndValue(String property, String value) throws NotFound;

    T findByMultipleValues(String clause, String... values) throws NotFound;

    int delete(String column, String ident, boolean value) throws DeleteFailed;

    T delete(T object) throws DeleteFailed;
}


