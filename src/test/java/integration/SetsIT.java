package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persistence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.SetService;
import org.junit.Assert;
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
public class SetsIT {
    private List<Set> sets = null;

    private SetService setService;

    @Autowired
    private SetDao setDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        setService = new SetService();
        setService.setDao(setDao);
    }

    @Test
    public void Save_single_set_object() throws SaveFailed {
        Set set = sets.get(0);
        set = setService.saveSet(set);
        Assert.assertNotNull(set.getIdentifier());
    }

    @Test
    public void Save_single_set_object_not_successful() throws SaveFailed {
        thrown.expect(SaveFailed.class);
        thrown.expectMessage("Creating Set failed, no ID obtained.");

        Set set = sets.get(0);
        set = setService.saveSet(set);
    }

    @Test
    public void Save_set_collection() throws SaveFailed {
        List<Set> data = setService.saveSets(sets);
        assertThat(data.size()).isGreaterThan(0);
    }

    @Test
    public void Save_set_collection_not_successful() throws SaveFailed {
        thrown.expect(SaveFailed.class);
        thrown.expectMessage("Creating Set failed, no ID obtained.");

        List<Set> data = setService.saveSets(sets);
    }

    @Test
    public void Find_all_sets() throws NotFound {
        List<Set> data = setService.findAll();
        assertThat(data.size()).isGreaterThan(0);
    }

    @Test
    public void Find_set_by_setspec() throws NotFound {
        Set set = (Set) setService.find("setspec", "ddc:1200").iterator().next();
        Assert.assertEquals("ddc:1200", set.getSetSpec());
    }

    @Test
    public void Find_set_by_setspec_not_successful() throws NotFound {
        thrown.expect(NotFound.class);
        thrown.expectMessage("Cannot found set.");
        setService.find("setspec", "ddc:120");
    }

    @Test
    public void Mark_set_as_deleted() throws DeleteFailed {
        Set set = sets.get(0);
        int deleted = setService.deleteSet("setspec", set.getSetSpec(), true);
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Mark_set_as_deleted_not_successful() throws DeleteFailed {
        thrown.expect(DeleteFailed.class);
        thrown.expectMessage("Cannot delete set.");
        int deleted = setService.deleteSet("setspec", "ddc:120", true);
    }

    @Test
    public void Undo_deleted() throws DeleteFailed {
        Set set = sets.get(0);
        int deleted = setService.deleteSet("setspec", set.getSetSpec(), false);
        assertThat(1).isEqualTo(deleted);
    }

    @Test
    public void Udate_set_data_row() throws UpdateFailed {
        Set set = sets.get(0);
        set.setSetDescription("palaber ganz doll viel");
        Set update = setService.updateSet(set, "ddc:1200");
        assertThat("palaber ganz doll viel").isEqualTo(update.getSetDescription());
    }

    @Test
    public void Udate_set_data_row_not_successful() throws UpdateFailed {
        thrown.expect(UpdateFailed.class);
        thrown.expectMessage("Cannot update set.");
        Set set = sets.get(0);
        set.setSetDescription("palaber ganz doll viel");
        Set update = setService.updateSet(set, "ddc:120");
    }
}
