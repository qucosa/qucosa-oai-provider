package de.qucosa.oai.provider.api.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class FormatApi<T> {

    private Dao dao;

    private T inputData;

    public FormatApi() {}

    public FormatApi(T input) throws IOException {
        if (input instanceof String) {
            ObjectMapper om = new ObjectMapper();
            String ip = (String) input;

            try {
                inputData = om.readValue(ip.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Format.class));
            } catch (IOException e) {

                try {
                    inputData = (T) om.readValue(ip.getBytes("UTF-8"), Format.class);
                } catch (IOException e1) {
                    throw e;
                }
            }
        }

        if (input instanceof Format) {
            inputData = input;
        }

        if (input instanceof List) {
            inputData = input;
        }

        setDao(new FormatDao<Format>());
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Format saveFormat(Format format) throws SaveFailed {
        return (Format) dao.saveAndSetIdentifier(format);
    }

    public Collection<Format> saveFormats(List<Format> formats) throws SaveFailed {
        return dao.saveAndSetIdentifier(formats);
    }

    public Format updateFormat(Format input, String mdprefix) throws Exception {

        if (!input.getMdprefix().equals(mdprefix)) {
            throw new Exception("Prameter mdprefix is unequal with mdprefix from format object.");
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
