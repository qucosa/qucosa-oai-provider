package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
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
    public void Save_dissemination() throws SaveFailed {
        Format format = null;

        try {
            format = (Format) formatApi.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound ignore) {

        }

        Dissemination dissemination = disseminations.get(0);
        assert format != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination = disseminationApi.saveDissemination(dissemination);

        if (dissemination != null) {
            assertThat(dissemination.getDissId()).isNotNull();
        }
    }

    @Test
    public void Find_dissemination_by_multiple_criterias() throws NotFound {
        Format format = (Format) formatApi.find("mdprefix", "oai_dc").iterator().next();
        Dissemination dissemination = disseminationApi.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        assertThat(dissemination.getDissId()).isNotNull();
        assertThat(dissemination.getRecordId()).isEqualTo("oai:example:org:qucosa:55887");
        assertThat(dissemination.getFormatId()).isEqualTo(format.getFormatId());
    }

    @Test
    public void Mark_dissemination_as_deleted() throws DeleteFailed, NotFound {
        Format format = null;

        try {
            format = (Format) formatApi.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound notFound) {
            throw new NotFound("Cannot find format.");
        }

        Dissemination dissemination = null;

        try {
            assert format != null;
            dissemination = disseminationApi.findByMultipleValues(
                    "id_format=%s AND id_record=%s",
                    String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        } catch (NotFound notFound) {
            throw new NotFound("Cannot find dissemination.");
        }

        assert dissemination != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(true);
        dissemination = disseminationApi.deleteDissemination(dissemination);
        assertThat(dissemination.isDeleted()).isTrue();
    }

    @Test
    public void Mark_dissemination_as_not_deleted() throws DeleteFailed {
        Format format = null;

        try {
            format = (Format) formatApi.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }

        Dissemination dissemination = null;

        try {
            assert format != null;
            dissemination = disseminationApi.findByMultipleValues(
                    "id_format=%s AND id_record=%s",
                    String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }

        assert dissemination != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(false);
        dissemination = disseminationApi.deleteDissemination(dissemination);
        assertThat(dissemination.isDeleted()).isFalse();
    }
}
