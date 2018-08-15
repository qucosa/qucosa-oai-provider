package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import de.qucosa.oai.provider.persitence.model.Format;
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
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
@FixMethodOrder(MethodSorters.JVM)
public class DisseminationIT {

    private List<Dissemination> disseminations = null;

    private DisseminationApi disseminationApi;

    private FormatApi formatApi;

    @Autowired
    private Dao disseminationDao;

    @Autowired
    private Dao formatDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        ObjectMapper om = new ObjectMapper();
        disseminations  = om.readValue(TestData.DISSEMINATIONS, om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
        disseminationApi = new DisseminationApi();
        disseminationApi.setDao(disseminationDao);
        formatApi = new FormatApi();
        formatApi.setDao(formatDao);
    }

    @Test
    public void Save_dissemination() throws SQLException {
        Format format = formatApi.find("mdprefix", "oai_dc");
        Dissemination dissemination = disseminations.get(0);
        dissemination.setFormatId(format.getFormatId());
        dissemination = disseminationApi.saveDissemination(dissemination);
        assertThat(dissemination.getDissId()).isNotNull();
    }

    @Test
    public void Find_dissemination_by_multiple_criterias() throws SQLException {
        Format format = formatApi.find("mdprefix", "oai_dc");
        Dissemination dissemination = disseminationApi.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        assertThat(dissemination.getDissId()).isNotNull();
        assertThat(dissemination.getRecordId()).isEqualTo("oai:example:org:qucosa:55887");
        assertThat(dissemination.getFormatId()).isEqualTo(format.getFormatId());
    }

    @Test
    public void Mark_dissemination_as_deleted() throws SQLException {
        Format format = formatApi.find("mdprefix", "oai_dc");
        Dissemination dissemination = disseminationApi.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(true);
        dissemination = disseminationApi.deleteDissemination(dissemination);
        assertThat(dissemination.isDeleted()).isTrue();
    }

    @Test
    public void Mark_dissemination_as_not_deleted() throws SQLException {
        Format format = formatApi.find("mdprefix", "oai_dc");
        Dissemination dissemination = disseminationApi.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(false);
        dissemination = disseminationApi.deleteDissemination(dissemination);
        assertThat(dissemination.isDeleted()).isFalse();
    }
}
