package de.qucosa.oai.provider.api.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.model.Record;

import java.io.IOException;
import java.util.List;

public class RecordApi<T> {
    private Dao<Record> dao;

    private T inputData;

    public RecordApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        dao = new RecordDao<>();

        try {
            inputData = om.readValue(input.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Record.class));
        } catch (IOException e) {

            try {
                inputData = (T) om.readValue(input.getBytes("UTF-8"), Record.class);
            } catch (IOException e1) {
                throw e;
            }
        }
    }

    public RecordApi(Record input) {
        this.inputData = (T) input;
        dao = new RecordDao<>();
    }

    public RecordApi(Dao<Record> dao, Record input) {
        this.inputData = (T) input;
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Record saveRecord() {
        return null;
    }

    public Record updateRecord() {
        return null;
    }

    public boolean deleteRecord() {
        return true;
    }

    public Record findRecord() {
        return null;
    }
}
