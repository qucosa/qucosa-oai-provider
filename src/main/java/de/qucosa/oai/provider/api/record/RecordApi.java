package de.qucosa.oai.provider.api.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.model.Record;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Component
public class RecordApi<T> {
    private Dao dao;

    private T inputData;

    public RecordApi() {}

    public RecordApi(T input) throws IOException {

        if (input instanceof String) {
            ObjectMapper om = new ObjectMapper();
            String ip = (String) input;

            try {
                inputData = om.readValue(ip.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Record.class));
            } catch (IOException e) {

                try {
                    inputData = (T) om.readValue(ip.getBytes("UTF-8"), Record.class);
                } catch (IOException e1) {
                    throw e;
                }
            }
        }

        if (input instanceof Record) {
            inputData = input;
        }

        if (input instanceof List) {
            inputData = input;
        }

        setDao(new RecordDao<>());
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Record saveRecord(Record record) throws SQLException {
        return (Record) dao.save(record);
    }

    public Record updateRecord(Record record) throws SQLException {
        return (Record) dao.update(record);
    }

    public Record deleteRecord(Record input) throws SQLException {
        Record record = (Record) dao.delete("uid", input.getUid(), input.isDeleted());
        return record;
    }

    public Record findRecord(String column, String uid) throws SQLException {
        Record record = (Record) dao.findByColumnAndValue(column, uid);
        return record;
    }
}
