package de.qucosa.oai.provider.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Service
public class FormatService<T> {

    private Dao dao;

    public FormatService() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Format saveFormat(Format format) throws SaveFailed {
        return (Format) dao.saveAndSetIdentifier(format);
    }

    public Collection<Format> saveFormats(List<Format> formats) throws SaveFailed {
        return dao.saveAndSetIdentifier(formats);
    }

    public Format updateFormat(Format input, String mdprefix) throws UpdateFailed {

        if (!input.getMdprefix().equals(mdprefix)) {
            throw new UpdateFailed("Prameter mdprefix is unequal with mdprefix from format object.");
        }

        return (Format) dao.update(input);
    }

    public Collection<Format> find(String column, String value) throws NotFound {
        return dao.findByPropertyAndValue(column, value);
    }

    public List<Format> findAll() throws NotFound {
        return (List<Format>) dao.findAll();
    }

    public int deleteFormat(String column, String ident, boolean value) throws DeleteFailed {
        return dao.delete(column, ident, value);
    }
}
