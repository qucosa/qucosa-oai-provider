package de.qucosa.oai.provider.api;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class SetApiTest {
    private String sets;

    private String set;

    private SetApi setApi;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
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
        setApi = new SetApi(set);
        Set data = setApi.saveSet();
        Assert.assertNotNull(data);
    }

    private static class SetTestDao<T> implements Dao<T> {

        @Override
        public T save(T object) {
            return null;
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
            return null;
        }

        @Override
        public T delete(String column, T value) {
            return null;
        }
    }
}
