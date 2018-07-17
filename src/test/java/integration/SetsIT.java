package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class SetsIT {
    private List<Set> sets = null;

    private SetApi setApi;

    @Autowired
    private SetDao setDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
        setApi = new SetApi();
        setApi.setDao(setDao);
    }

    @Test
    public void Find_all_sets() throws SQLException {
        List<Set> data = setApi.findAll();
        Assert.assertEquals(2, data.size());
    }

    @Test
    public void Find_set_by_setspec() throws SQLException {
        Set set = setApi.find("setspec", "ddc:1200");
        Assert.assertEquals("ddc:1200", set.getSetSpec());
    }
}
