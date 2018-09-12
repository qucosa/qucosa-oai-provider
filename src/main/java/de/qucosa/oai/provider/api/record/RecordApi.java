package de.qucosa.oai.provider.api.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.RecordTransport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class RecordApi<T> {
    private Dao dao;

    public RecordApi() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Record saveRecord(Record record) throws SaveFailed {
        return (Record) dao.saveAndSetIdentifier(record);
    }

    public Record updateRecord(Record record) throws UpdateFailed {
        return (Record) dao.update(record);
    }

    public int deleteRecord(Record input) throws DeleteFailed {
        return dao.delete("uid", input.getUid(), input.isDeleted());
    }

    public Collection<Record> findRecord(String column, String uid) throws NotFound {
        return dao.findByPropertyAndValue(column, uid);
    }

    public boolean checkIfOaiDcDisseminationExists(List<RecordTransport> input) {
        boolean exists = false;

        for (RecordTransport rt : input) {

            if (!rt.getFormat().getMdprefix().equals("oai_dc")) {
                continue;
            }

            exists = true;
        }

        return exists;
    }
}
