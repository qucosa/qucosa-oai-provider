package de.qucosa.oai.provider.api.sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class SetApi<T> {
    private Dao dao;

    private T inputData;

    public SetApi() {}

    public SetApi(T input) throws IOException {

        if (input instanceof String) {
            ObjectMapper om = new ObjectMapper();
            String ip = (String) input;

            try {
                inputData = om.readValue(ip.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (IOException e) {

                try {
                    inputData = (T) om.readValue(ip.getBytes("UTF-8"), Set.class);
                } catch (IOException e1) {
                    throw e;
                }
            }
        }

        if (input instanceof Set) {
            inputData = input;
        }

        if (input instanceof List) {
            inputData = input;
        }

        setDao(new SetDao<>());
    }

    public void setDao(Dao<Set> dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Set saveSet(Set input) throws SaveFailed {
        return (Set) dao.saveAndSetIdentifier(input);
    }

    public List<Set> saveSets(List<Set> input) throws SaveFailed {
        return (List<Set>) dao.saveAndSetIdentifier(input);
    }

    public List<Set> findAll() throws NotFound {
        return (List<Set>) dao.findAll();
    }

    public Collection<Set> find(String column, String setspec) throws NotFound {
        return dao.findByPropertyAndValue(column, setspec);
    }

    public Set updateSet(Set input, String setspec) throws UpdateFailed {
        Set output;

        if (!input.getSetSpec().equals(setspec)) {
            throw new UpdateFailed("Prameter setspec is unequal with setpec from set object.");
        }

        output = (Set) dao.update(input);

        return output;
    }

    public int deleteSet(String column, String setspec, boolean value) throws DeleteFailed {
        return dao.delete(column, setspec, value);
    }
}
