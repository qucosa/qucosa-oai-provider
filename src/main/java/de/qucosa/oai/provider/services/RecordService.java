/**
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.services;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
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

    public void deleteRecord(String uid) throws DeleteFailed {
        dao.delete(uid);
    }

    public void undoDeleteRecord(String uid) throws UndoDeleteFailed {
        dao.undoDelete(uid);
    }

    public Collection<Record> findRecord(String column, String uid) throws NotFound {
        return dao.findByPropertyAndValue(column, uid);
    }

    public Collection<Record> findAll() throws NotFound {
        return dao.findAll();
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
