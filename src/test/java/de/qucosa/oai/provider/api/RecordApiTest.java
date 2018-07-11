package de.qucosa.oai.provider.api;

import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.persitence.model.Record;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

public class RecordApiTest {
    private String records;

    private String record;

    private RecordApi recordApi;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        record = "{\"pid\" : \"qucosa:55887\", \"uid\" : \"oai:example:org:qucosa:55887\"}";

        records = "[{\"pid\" : \"qucosa:55887\", \"uid\" : \"oai:example:org:qucosa:55887\"}," +
                "{\"pid\" : \"qucosa:55666\", \"uid\" : \"oai:example:org:qucosa:55666\"}]";
    }

    @Test
    public void Delivery_one_record_string_on_api() throws IOException {
        recordApi = new RecordApi(record);
        Record data = (Record) recordApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getPid());
        Assert.assertNotNull(data.getUid());
    }

    @Test
    public void Delivery_json_record_array_string_on_api() throws IOException {
        recordApi = new RecordApi(records);
        List<Record> data = (List<Record>) recordApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertEquals(2, data.size());
    }
}
