package de.qucosa.oai.provider.api.sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Set;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SetApi<T> {
    private Dao<Set> dao;

    private T inputData;

    public SetApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        dao = new SetDao<Set>();

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

    public Set saveSet() throws SQLException {
        return dao.save((Set) getInputData());
    }

    public Set updateSet() {
        return null;
    }

    public boolean deleteSet() {
        return true;
    }
}
