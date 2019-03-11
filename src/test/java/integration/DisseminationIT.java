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
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class DisseminationIT {

    private List<Dissemination> disseminations = null;

    private DisseminationService disseminationService;

    private FormatService formatService;

    @Autowired
    private Dao disseminationDao;

    @Autowired
    private Dao formatDao;


    @BeforeAll
    public void init() throws IOException {
        ObjectMapper om = new ObjectMapper();
        disseminations  = om.readValue(TestData.DISSEMINATIONS, om.getTypeFactory().constructCollectionType(List.class, Dissemination.class));
        disseminationService = new DisseminationService();
        disseminationService.setDao(disseminationDao);
        formatService = new FormatService();
        formatService.setDao(formatDao);
    }

    @Test
    public void Save_dissemination() throws SaveFailed {
        Format format = null;

        try {
            format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound ignore) {

        }

        Dissemination dissemination = disseminations.get(0);
        assert format != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination = disseminationService.saveDissemination(dissemination);

        if (dissemination != null) {
            assertThat(dissemination.getDissId()).isNotNull();
        }
    }

    @Test
    public void Find_dissemination_by_multiple_criterias() throws NotFound {
        Format format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        assertThat(dissemination.getDissId()).isNotNull();
        assertThat(dissemination.getRecordId()).isEqualTo("oai:example:org:qucosa:55887");
        assertThat(dissemination.getFormatId()).isEqualTo(format.getFormatId());
    }

    @Test
    public void Find_dissemination_by_multiple_criterias_not_successful_if_exists() throws NotFound {
        Format format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "oai:example:org:qucosa:5887");
        assertThat(dissemination).isNull();
    }

    @Test
    public void Find_dissemination_by_multiple_criterias_not_successful_because_format_id_failed() throws NotFound {
        //thrown.expect(NotFound.class);
        //thrown.expectMessage("Cannot find dissemination becaue record_id or format_id failed.");

        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                null, String.valueOf("oai:example:org:qucosa:55887"));
    }

    @Test
    public void Find_dissemination_by_multiple_criterias_not_successful_because_record_id_failed() throws NotFound {
        //thrown.expect(NotFound.class);
        //thrown.expectMessage("Cannot find dissemination becaue record_id or format_id failed.");

        Format format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        Dissemination dissemination = disseminationService.findByMultipleValues(
                "id_format=%s AND id_record=%s",
                String.valueOf(format.getFormatId()), "");
    }

    @Test
    public void Mark_dissemination_as_deleted() throws DeleteFailed, NotFound, UndoDeleteFailed {
        Format format;

        try {
            format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound notFound) {
            throw new NotFound("Cannot find format.");
        }

        assert format != null;
        Dissemination dissemination;

        try {
            dissemination = disseminationService.findByMultipleValues(
                    "id_format=%s AND id_record=%s",
                    String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        } catch (NotFound notFound) {
            throw new NotFound("Cannot find dissemination.");
        }

        assert dissemination != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(true);
        disseminationService.deleteDissemination(dissemination, null);
        assertThat(dissemination.isDeleted()).isTrue();
    }

    @Test
    public void Mark_dissemination_as_not_deleted() throws DeleteFailed, UndoDeleteFailed {
        Format format = null;

        try {
            format = (Format) formatService.find("mdprefix", "oai_dc").iterator().next();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }

        Dissemination dissemination = null;

        try {
            assert format != null;
            dissemination = disseminationService.findByMultipleValues(
                    "id_format=%s AND id_record=%s",
                    String.valueOf(format.getFormatId()), "oai:example:org:qucosa:55887");
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }

        assert dissemination != null;
        dissemination.setFormatId(format.getFormatId());
        dissemination.setDeleted(false);
        disseminationService.deleteDissemination(dissemination, "undo");
        assertThat(dissemination.isDeleted()).isFalse();
    }
}
