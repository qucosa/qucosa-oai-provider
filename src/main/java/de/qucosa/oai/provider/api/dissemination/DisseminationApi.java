package de.qucosa.oai.provider.api.dissemination;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.model.Dissemination;

import java.io.IOException;
import java.sql.SQLException;
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

    public Dissemination saveDissemination(Dissemination dissemination) throws SQLException {
        return (Dissemination) dao.save(dissemination);
    }

    public Dissemination updateDissemination() {
        return null;
    }

    public boolean deleteDissemination() {
        return true;
    }

    public Dissemination find(String column, T value) {
        return null;
    };
}
