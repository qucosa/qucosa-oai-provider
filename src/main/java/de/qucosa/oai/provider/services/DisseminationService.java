package de.qucosa.oai.provider.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;

import java.util.Collection;

public class DisseminationService<T> {
    private Dao dao;

    public DisseminationService() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dissemination saveDissemination(Dissemination dissemination) throws SaveFailed {
        return (Dissemination) dao.saveAndSetIdentifier(dissemination);
    }

    public Dissemination updateDissemination() {
        return null;
    }

    public Dissemination deleteDissemination(Dissemination dissemination) throws DeleteFailed {
        return (Dissemination) dao.delete(dissemination);
    }

    public Collection<Dissemination> findAllByUid(String property, String value) throws NotFound {
        return dao.findByPropertyAndValue(property, value);
    }

    public Dissemination findByMultipleValues(String clause, String... values) throws NotFound {
        return (Dissemination) dao.findByMultipleValues(clause, values);
    }
}
