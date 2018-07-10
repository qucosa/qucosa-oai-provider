package de.qucosa.oai.provider.api.sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Set;

import java.io.IOException;

public class SetApi {
    private Dao<Set> dao;

    private Set input;

    public SetApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        this.input = om.readValue(input, Set.class);
        dao = new SetDao<Set>();
    }

    public SetApi(Set input) {
        this.input = input;
        dao = new SetDao<Set>();
    }

    public SetApi(Dao<Set> dao, Set input) {
        this.input = input;
        this.dao = dao;
    }
}
