package de.qucosa.oai.provider.api;

import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DisseminationApiTest {
    private String dissemination;

    private String disseminations;

    private DisseminationApi disseminationApi;

    @Before
    public void init() {
        dissemination = "{\"recordid\" : 1, \"formatid\" : 1, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><oai_dc:dc xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:dc\\\" xmlns:oai_dc=\\\"http://www.openarchives.org/OAI/2.0/oai_dc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></oai_dc:dc>\"}";

        disseminations = "[{\"recordid\" : 1, \"formatid\" : 1, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><oai_dc:dc xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:dc\\\" xmlns:oai_dc=\\\"http://www.openarchives.org/OAI/2.0/oai_dc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></oai_dc:dc>\"}," +
                "{\"recordid\" : 1, \"formatid\" : 1, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?>\\n<xMetaDiss:xMetaDiss xmlns:cc=\\\"http://www.d-nb.de/standards/cc/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dcterms=\\\"http://purl.org/dc/terms/\\\" xmlns:ddb=\\\"http://www.d-nb.de/standards/ddb/\\\" xmlns:dini=\\\"http://www.d-nb.de/standards/xmetadissplus/type/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:xmetadissplus\\\" xmlns:pc=\\\"http://www.d-nb.de/standards/pc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:subject=\\\"http://www.d.nb.de/standards/subject/\\\" xmlns:thesis=\\\"http://www.ndltd.org/standards/metadata/etdms/1.0/\\\" xmlns:urn=\\\"http://www.d-nb.de/standards/urn/\\\" xmlns:xMetaDiss=\\\"http://www.d-nb.de/standards/xmetadissplus/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></xMetaDiss:xMetaDiss>\"}]";
    }

    @Test
    public void Delivery_one_dissemination_string_on_api() throws IOException {
        disseminationApi = new DisseminationApi(dissemination);
        Dissemination data = (Dissemination) disseminationApi.getInputData();
        Assert.assertNotNull(data);
    }

    @Test
    public void Delivery_json_dissemination_array_string_on_api() throws IOException {
        disseminationApi = new DisseminationApi(disseminations);
        List<Dissemination> data = (List<Dissemination>) disseminationApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertEquals(2, data.size());
    }
}
