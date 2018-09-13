package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
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
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
@FixMethodOrder(MethodSorters.JVM)
public class FormatsIT {

    private List<Format> formats = null;

    private FormatService formatService;

    @Autowired
    private Dao formatDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        formats = om.readValue(TestData.FORMATS, om.getTypeFactory().constructCollectionType(List.class, Format.class));
        formatService = new FormatService();
        formatService.setDao(formatDao);
    }

    @Test
    public void Save_single_format_object() throws SaveFailed {
        Format format = formats.get(0);
        format = formatService.saveFormat(format);
        assertThat(format.getFormatId()).isNotNull();
    }

    @Test
    public void Save_single_format_object_not_successful() throws SaveFailed {
        thrown.expect(SaveFailed.class);
        thrown.expectMessage("Cannot save format.");

        Format format = formats.get(0);
        formatService.saveFormat(format);
    }

    @Test
    public void Save_format_collection() throws SaveFailed {
        Collection<Format> response = formatService.saveFormats(formats);
        assertThat(response.size()).isGreaterThan(0);
    }

    @Test
    public void Update_format() throws Exception {
        Format format = formats.get(0);
        format.setNamespace("quatsch");
        format = formatService.updateFormat(format, "oai_dc");
        assertThat(format.getNamespace().trim()).isEqualTo("quatsch");
    }

    @Test
    public void Find_format_by_mdprefix_successful() throws NotFound {
        Format format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        assertThat(format).isNotNull();
        assertThat(format.getMdprefix().trim()).isEqualTo("oai_dc");
    }

    @Test
    public void Find_format_by_mdprefix_is_null() throws NotFound {
        Collection<Format> formats = formatService.find("mdprefix", "oai_c");
        assertThat(formats).isNull();
    }

    @Test
    public void Find_all_formats() throws NotFound {
        List<Format> response = formatService.findAll();
        assertThat(response).isNotNull();
        assertThat(response.size()).isGreaterThan(0);
    }

    @Test
    public void Marked_format_as_deleted() throws DeleteFailed {
        Format format = formats.get(0);
        int deletetd = formatService.deleteFormat("mdprefix", format.getMdprefix(), true);
        assertThat(1).isEqualTo(deletetd);
    }

    @Test
    public void Marked_format_as_deleted_not_successful() throws DeleteFailed {
        thrown.expect(DeleteFailed.class);
        thrown.expectMessage("Cannot delete format.");

        formatService.deleteFormat("mdprefix", "oia_d", true);
    }

    @Test
    public void Marked_format_as_not_deleted() throws DeleteFailed {
        Format format = formats.get(0);
        int deletetd = formatService.deleteFormat("mdprefix", format.getMdprefix(), false);
        assertThat(1).isEqualTo(deletetd);
    }
}
