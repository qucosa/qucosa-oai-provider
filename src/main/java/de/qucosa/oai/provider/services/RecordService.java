/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qucosa.oai.provider.services;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Record;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RecordService {
    private Dao<Record> dao;

    public RecordService() {}

    public void setDao(Dao<Record> dao) {
        this.dao = dao;
    }

    public Record saveRecord(Record record) throws SaveFailed {
        return dao.saveAndSetIdentifier(record);
    }

    public Record updateRecord(Record record, String oaiId) throws UpdateFailed {

        if (!record.getOaiid().equals(oaiId) || oaiId.isEmpty()) {
            throw new UpdateFailed("Unequal oaiid parameter with record object oaiid.");
        }

        return dao.update(record);
    }

    public void delete(Record record) throws DeleteFailed {
        dao.delete(record.getOaiid());
    }

    public Collection<Record> findRecord(String column, String oaiid) throws NotFound {
        return dao.findByPropertyAndValue(column, oaiid);
    }

    public Collection<Record> findAll() throws NotFound {
        return dao.findAll();
    }

    public Collection<Record> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return dao.findRowsByMultipleValues(clause, values);
    }

    /*public boolean checkIfOaiDcDisseminationExists(OaiRecord input) {
        boolean exists = false;

        for (OaiRecord rt : input) {

            if (!rt.getFormat().getMdprefix().equals("oai_dc")) {
                continue;
            }

            exists = true;
        }

        return exists;
    }*/
}
