package de.qucosa.oai.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.config.ApplicationConfig;
import de.qucosa.oai.provider.persitence.Dao;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ApplicationConfig.class})
public class SetApiTest {
    private static String sets;

    private static String set;

    private SetApi setApi;

//    @Autowired
//    private ComboPooledDataSource dataSource;

    @Autowired
    private SetDao setDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws SQLException {
        set = "{\"setspec\": \"ddc:1200\", \"setname\": \"Test Set 1200\", \"setdescription\" : \"\"}";

        sets = "[{\"setspec\": \"ddc:1200\", \"setname\": \"Test Set 1200\", \"setdescription\" : \"\"}," +
                "{\"setspec\": \"ddc:1201\", \"setname\": \"Test Set 1201\", \"setdescription\" : \"\"}]";
    }

    @Test
    public void Delivery_one_set_string_on_api() throws IOException {
        setApi = new SetApi(set);
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
//        setDao.setConnection(dataSource);
        setApi = new SetApi(new SetTestDao<>());
        Set data = setApi.saveSet(om.readValue(set, Set.class));
        Assert.assertNotNull(data);
        Assert.assertEquals(new Long(1), data.getSetId());
    }

    @Test
    public void Find_set_by_setspec_column() throws IOException, SQLException {
        ObjectMapper om = new ObjectMapper();
        setApi = new SetApi(new SetTestDao<>(), om.readValue(set, Set.class));
        Set set = setApi.find("setspec", "ddc:1200");
        Assert.assertEquals("ddc:1200", set.getSetSpec());
    }

    @Test
    public void Delete_set_by_column_and_value() throws SQLException {
        setApi = new SetApi(new SetTestDao<>());
        Long setId = setApi.deleteSet("setspec", "ddc:1200");
        Assert.assertEquals(Long.valueOf(1), setId);
    }

    @Test
    public void Update_set_by_setspec() {

    }

    private static class SetTestDao<T> implements Dao<T> {

        @Override
        public T save(T object) {
            Set set = (Set) object;
            set.setSetId(new Long(1));
            return (T) set;
        }

        @Override
        public T save(Collections objects) {
            return null;
        }

        @Override
        public T update(T object) {
            return null;
        }

        @Override
        public T update(Collections objects) {
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
                sets = om.readValue(SetApiTest.sets, om.getTypeFactory().constructCollectionType(List.class, Set.class));
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
                JsonNode nodes = om.readTree(SetApiTest.sets);

                for (JsonNode entry : nodes) {

                    if (!entry.has(column)) {
                        throw new SQLException("Set mark as deleted failed, no rwos affected.");
                    }

                    if (!entry.get(column).equals(value)) {
                        throw new SQLException("Set mark as deleted failed, no rwos affected.");
                    }

                    set = om.readValue(entry.toString(), Set.class);
                    set.setSetId(new Long(1));
                    set.setDeleted(true);
                }
            } catch (IOException e) { }

            return (T) set.getSetId();
        }
    }
}
