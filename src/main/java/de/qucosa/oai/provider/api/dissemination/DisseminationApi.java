package de.qucosa.oai.provider.api.dissemination;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DisseminationApi<T> {
    private Dao dao;

    private T inputData;

    public DisseminationApi() {}

    public DisseminationApi(T input) throws IOException {

        if (input instanceof String) {
            ObjectMapper om = new ObjectMapper();
            String ip = (String) input;

            try {
                inputData = om.readValue(ip.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
            } catch (IOException e) {

                try {
                    inputData = (T) om.readValue(ip.getBytes("UTF-8"), Dissemination.class);
                } catch (IOException e1) {
                    throw e;
                }
            }
        }

        if (input instanceof Dissemination) {
            inputData = input;
        }

        if (input instanceof List) {
            inputData = input;
        }

        setDao(new DisseminationDao<Dissemination>());
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
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
