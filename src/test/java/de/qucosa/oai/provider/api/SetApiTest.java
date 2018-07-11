package de.qucosa.oai.provider.api;

import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.model.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

public class SetApiTest {
    private String sets;

    private String set;

    private SetApi setApi;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        set = "{\"setspec\": \"ddc:980\", \"setname\": \"General history of South America\", \"setdescription\" : \"\"}";

        sets = "[{\"setspec\": \"ddc:980\", \"setname\": \"General history of South America\", \"setdescription\" : \"\"}," +
                "{\"setspec\": \"ddc:990\", \"setname\": \"General history of other areas\", \"setdescription\" : \"\"}]";
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
}
