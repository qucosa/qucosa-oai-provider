package de.qucosa.oai.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import testdata.TestData;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class SetApiTest {
    private SetApi setApi;

    private List<Set> sets = null;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException {
        ObjectMapper om = new ObjectMapper();
        sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
    }

    @Test
    public void Delivery_one_set_string_on_api() throws IOException {
        setApi = new SetApi(sets.get(0));
        Set data = (Set) setApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getSetSpec());
        Assert.assertNotNull(data.getSetName());
    }

    @Test
    public void Delivery_json_set_array_string_on_api() throws IOException {
        setApi = new SetApi(sets);
        List<Set> data = (List<Set>) setApi.getInputData();
        Assert.assertNotNull(data);
        Assert.assertEquals(2, data.size());
    }

    @Test
    public void Save_set_object() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        setApi = new SetApi();
        setApi.setDao(new SetTestDao<>());
        Set data = setApi.saveSet(sets.get(0));
        Assert.assertNotNull(data);
        Assert.assertEquals(new Long(1), data.getSetId());
    }

    @Test
    public void Save_multible_set_collection() throws SQLException {
        setApi = new SetApi();
        setApi.setDao(new SetTestDao());
        List<Set> data = setApi.saveSets(sets);
        Assert.assertEquals(2, data.size());
    }

    @Test
    public void Find_set_by_setspec_column() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        setApi = new SetApi(sets);
        setApi.setDao(new SetTestDao<>());
        Set set = setApi.find("setspec", "ddc:1200");
        Assert.assertEquals("ddc:1200", set.getSetSpec());
    }

    @Test
    public void Delete_set_by_column_and_value() throws SQLException {
        setApi = new SetApi();
        setApi.setDao(new SetTestDao<>());
        Long setId = setApi.deleteSet("setspec", "ddc:1200");
        Assert.assertEquals(Long.valueOf(1), setId);
    }

    private static class SetTestDao<T> implements Dao<T> {

        @Override
        public T save(T object) {
            Set set = (Set) object;
            set.setSetId(new Long(1));
            return (T) set;
        }

        @Override
        public T save(Collection objects) {
            int i = 0;

            for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
                i++;
                Set set = (Set) iterator.next();
                set.setSetId(Long.valueOf(i));
            }

            return (T) objects;
        }

        @Override
        public T update(T object) {
            return null;
        }

        @Override
        public T update(Collection objects) {
            return null;
        }

        @Override
        public T findAll() {
            return null;
        }

        @Override
        public T findById(T value) {
            return null;
        }

        @Override
        public T findByColumnAndValue(String column, T value) {
            ObjectMapper om = new ObjectMapper();
            Set set = null;
            List<Set> sets = null;

            try {
                sets = om.readValue(TestData.SETS, om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (IOException e) { }

            for (Set obj : sets) {

                if (obj.getSetSpec().equals(value)) {
                    set = obj;
                    break;
                }
            }

            return (T) set;
        }

        @Override
        public T delete(String column, T value) throws SQLException {
            ObjectMapper om = new ObjectMapper();
            Set set = null;

            try {
                JsonNode nodes = om.readTree(TestData.SETS);

                for (JsonNode entry : nodes) {

                    if (!entry.has(column)) {
                        throw new SQLException("Set mark as deleted failed, no rwos affected.");
                    }

                    if (entry.get(column).asText().equals(value)) {
                        set = om.readValue(entry.toString(), Set.class);
                        set.setSetId(new Long(1));
                        set.setDeleted(true);
                    }
                }
            } catch (IOException e) { }

            return (T) set.getSetId();
        }
    }
}
