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

    public SetApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        dao = new SetDao<>();

        try {
            inputData = om.readValue(input.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Set.class));
        } catch (IOException e) {

            try {
                inputData = (T) om.readValue(input.getBytes("UTF-8"), Set.class);
            } catch (IOException e1) {
                throw e;
            }
        }
    }

    public SetApi(Dao<Set> setDao) {
        this.dao = setDao;
    }

    public SetApi(Set input) {
        this.inputData = (T) input;
        dao = new SetDao<Set>();
    }

    public SetApi(Dao<Set> dao, Set input) {
        this.inputData = (T) input;
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Set saveSet(Set input) throws SQLException {
        return (Set) dao.save(input);
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

    public Long deleteSet(String column, String setspec) throws SQLException {
        return (Long) dao.delete(column, setspec);
    }
}
