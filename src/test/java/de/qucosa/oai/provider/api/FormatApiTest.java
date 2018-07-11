package de.qucosa.oai.provider.api;

import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.persitence.model.Format;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class FormatApiTest {
    private String format;

    private String formats;

    private FormatApi formatApi;

    @Before
    public void init() {
        format = "{\"mdprefix\" : \"oai_dc\", \"schemaurl\" : \"http://www.openarchives.org/OAI/2.0/oai_dc/\", \"namespace\" : \"oai_dc\"}";

        formats = "[{\"mdprefix\" : \"oai_dc\", \"schemaurl\" : \"http://www.openarchives.org/OAI/2.0/oai_dc/\", \"namespace\" : \"oai_dc\"}, " +
                "{\"mdprefix\" : \"xmetadissplus\", \"schemaurl\" : \"http://www.d-nb.de/standards/xmetadissplus/\", \"namespace\" : \"xMetaDiss\"}]";
    }

    @Test
    public void Delivery_one_format_string_on_api() throws IOException {
        formatApi = new FormatApi(format);
        Format data = (Format) formatApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertEquals("oai_dc", data.getMdprefix());
        Assert.assertEquals("http://www.openarchives.org/OAI/2.0/oai_dc/", data.getSchemaUrl());
        Assert.assertEquals("oai_dc", data.getNamespace());
    }

    @Test
    public void Delivery_json_format_array_string_on_api() throws IOException {
        formatApi = new FormatApi(formats);
        List<Format> data = (List<Format>) formatApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertEquals(2, data.size());
    }
}
