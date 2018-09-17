package de.qucosa.oai.provider.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.RecordTransport;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class RecordService<T> {
    private Dao dao;

    public RecordService() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Record saveRecord(Record record) throws SaveFailed {
        return (Record) dao.saveAndSetIdentifier(record);
    }

    public Record updateRecord(Record record, String uid) throws UpdateFailed {

        if (!record.getUid().equals(uid) || uid.isEmpty()) {
            throw new UpdateFailed("Unequal uid parameter with record object uid.");
        }

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
