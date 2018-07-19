package de.qucosa.oai.provider.api.sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Set;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Set saveSet(Set input) throws SQLException {
        return (Set) dao.save(input);
    }

    public List<Set> saveSets(List<Set> input) throws SQLException {
        return (List<Set>) dao.save(input);
    }

    public List<Set> findAll() throws SQLException {
        return (List<Set>) dao.findAll();
    }

    public Set find(String column, String setspec) throws SQLException {
        return (Set) dao.findByColumnAndValue(column, setspec);
    }

    public Set updateSet(Set input, String setspec) throws Exception {
        Set output;

        if (!input.getSetSpec().equals(setspec)) {
            throw new Exception("Prameter setspec is unequal with setpec from set object.");
        }

        output = (Set) dao.update(input);

        return output;
    }

    public Set deleteSet(String column, String setspec, boolean value) throws SQLException {
        return (Set) dao.delete(column, setspec, value);
    }
}
