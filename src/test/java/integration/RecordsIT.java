package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persitence.exceptions.NotFound;
import de.qucosa.oai.provider.persitence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persitence.model.Record;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
@FixMethodOrder(MethodSorters.JVM)
public class RecordsIT {

    private List<Record> records = null;

    private RecordApi recordApi;

    @Autowired
    private Dao recordDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        ObjectMapper om = new ObjectMapper();
        records = om.readValue(TestData.RECORDS, om.getTypeFactory().constructCollectionType(List.class, Record.class));
        recordApi = new RecordApi();
        recordApi.setDao(recordDao);
    }

    @Test
    public void Save_record_object() throws SaveFailed {
        Record record = records.get(0);
        record = recordApi.saveRecord(record);
        assertThat(record.getRecordId()).isNotNull();
    }

    @Test
    public void Find_Record_by_uid() throws NotFound {
        Record record = records.get(0);
        Record result = (Record) recordApi.findRecord("uid", record.getUid()).iterator().next();
        assertThat(result).isNotNull();
    }

    @Test
    public void Mark_record_as_deleted() throws DeleteFailed {
        Record record = records.get(0);
        record.setDeleted(true);
        int deleted = recordApi.deleteRecord(record);
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Mark_record_as_not_deleted() throws DeleteFailed {
        Record record = records.get(0);
        record.setDeleted(false);
        int deleted = recordApi.deleteRecord(record);
        assertThat(1).isEqualTo(deleted);
    }
}
