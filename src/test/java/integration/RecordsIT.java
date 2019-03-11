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
package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.services.RecordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class RecordsIT {

    private List<Record> records = null;

    private RecordService recordService;

    @Autowired
    private Dao recordDao;

    @BeforeAll
    public void init() throws IOException {
        ObjectMapper om = new ObjectMapper();
        records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));
        recordService = new RecordService();
        recordService.setDao(recordDao);
    }

    @Test
    public void Save_record_object() throws SaveFailed {
        Record record = records.get(0);
        record = recordService.saveRecord(record);
        assertThat(record.getRecordId()).isNotNull();
    }

    @Test
    public void Find_Record_by_uid() throws NotFound {
        Record record = records.get(0);
        Record result = (Record) recordService.findRecord("uid", record.getUid()).iterator().next();
        assertThat(result).isNotNull();
    }

    @Test
    public void Mark_record_as_deleted() throws DeleteFailed {
        Record record = records.get(0);
        recordService.deleteRecord(record.getUid());
    }

    @Test
    public void Undo_mark_record_as_not_deleted() throws UndoDeleteFailed {
        Record record = records.get(0);
        recordService.undoDeleteRecord(record.getUid());
    }
}
