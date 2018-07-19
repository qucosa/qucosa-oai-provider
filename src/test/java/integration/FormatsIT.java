package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Set;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
@FixMethodOrder(MethodSorters.JVM)
public class FormatsIT {

    private List<Format> formats = null;

    private FormatApi formatApi;

    @Autowired
    private Dao formatDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
        formatApi = new FormatApi();
        formatApi.setDao(formatDao);
    }

    @Test
    public void Save_single_format_object() throws SQLException {
        Format format = formats.get(0);
        format = formatApi.saveFormat(format);
        assertThat(format.getFormatId()).isNotNull();
    }

    @Test
    public void Save_format_collection() throws SQLException {
        List<Format> response = formatApi.saveFormats(formats);
        assertThat(response.size()).isGreaterThan(0);
    }

    @Test
    public void Update_format() throws Exception {
        Format format = formats.get(0);
        format.setNamespace("quatsch");
        format = formatApi.updateFormat(format, "oai_dc");
        assertThat(format.getNamespace().trim()).isEqualTo("quatsch");
    }

    @Test
    public void Find_format_by_mdprefix() throws SQLException {
        Format format = formatApi.find("mdprefix", "oai_dc");
        assertThat(format).isNotNull();
        assertThat(format.getMdprefix().trim()).isEqualTo("oai_dc");
    }

    @Test
    public void Find_all_formats() throws SQLException {
        List<Format> response = formatApi.findAll();
        assertThat(response).isNotNull();
        assertThat(response.size()).isGreaterThan(0);
    }

    @Test
    public void Marked_format_as_deleted() throws SQLException {
        Format format = formats.get(0);
        format = formatApi.deleteFormat("mdprefix", format.getMdprefix(), true);
        assertThat(format.isDeleted()).isEqualTo(true);
    }

    @Test
    public void Marked_format_as_not_deleted() throws SQLException {
        Format format = formats.get(0);
        format = formatApi.deleteFormat("mdprefix", format.getMdprefix(), false);
        assertThat(format.isDeleted()).isEqualTo(false);
    }
}
