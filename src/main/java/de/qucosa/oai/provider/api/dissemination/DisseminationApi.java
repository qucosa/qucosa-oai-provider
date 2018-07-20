package de.qucosa.oai.provider.api.dissemination;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.model.Dissemination;

import java.io.IOException;
import java.util.List;

public class DisseminationApi<T> {
    private Dao dao;

    private T inputData;

    public DisseminationApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        dao = new DisseminationDao<>();

        try {
            inputData = om.readValue(input.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
        } catch (IOException e) {

            try {
                inputData = (T) om.readValue(input.getBytes("UTF-8"), Dissemination.class);
            } catch (IOException e1) {
                throw e;
            }
        }
    }

    public DisseminationApi(Dissemination input) {
        this.inputData = (T) input;
        dao = new DisseminationDao<Dissemination>();
    }

    public DisseminationApi(Dao dao, Dissemination input) {
        this.inputData = (T) input;
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Dissemination saveDissemination() {
        return null;
    }

    public Dissemination updateDissemination() {
        return null;
    }

    public boolean deleteDissemination() {
        return true;
    }
}
