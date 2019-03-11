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
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.FormatService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class FormatsIT {

    private List<Format> formats = null;

    private FormatService formatService;

    @Autowired
    private Dao formatDao;

    @BeforeAll
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
        //thrown.expect(SaveFailed.class);
        //thrown.expectMessage("Cannot save format.");

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
    public void Mark_format_as_deleted() throws DeleteFailed {
        Format format = formats.get(0);
        formatService.deleteFormat(format.getMdprefix());
    }

    @Test
    public void Mark_format_as_deleted_not_successful_if_mdprefix_is_wrong() throws DeleteFailed {
        //thrown.expect(DeleteFailed.class);
        //thrown.expectMessage("Cannot delete format.");

        formatService.deleteFormat("oia_d");
    }

    @Test
    public void Undo_mark_format_as_deleted() throws UndoDeleteFailed {
        Format format = formats.get(0);
        formatService.undoDeleteFormat(format.getMdprefix());
    }
}
