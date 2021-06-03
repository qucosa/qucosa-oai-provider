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

    public Record saveRecord(Record record) {
        return dao.saveAndSetIdentifier(record);
    }

    public Record updateRecord(Record record, String oaiId) {

        if (!record.getOaiid().equals(oaiId) || oaiId.isEmpty()) {
            //throw new UpdateFailed("Unequal oaiid parameter with record object oaiid.");
        }

        return dao.update(record);
    }

    public void delete(Record record) {
        dao.delete(record.getOaiid());
    }

    public Collection<Record> findRecord(String column, String oaiid) {
        return dao.findByPropertyAndValue(column, oaiid);
    }

    public Collection<Record> findAll() {
        return dao.findAll();
    }

    public Collection<Record> findRowsByMultipleValues(String clause, String... values) {
        return dao.findRowsByMultipleValues(clause, values);
    }
}
