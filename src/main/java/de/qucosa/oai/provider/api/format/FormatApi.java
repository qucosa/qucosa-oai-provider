package de.qucosa.oai.provider.api.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.model.Format;

import java.io.IOException;
import java.util.List;

public class FormatApi<T> {

    private Dao<Format> dao;

    private T inputData;

    public FormatApi(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        dao = new FormatDao<>();

        try {
            inputData = om.readValue(input.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Format.class));
        } catch (IOException e) {

            try {
                inputData = (T) om.readValue(input.getBytes("UTF-8"), Format.class);
            } catch (IOException e1) {
                throw e;
            }
        }
    }

    public FormatApi(Format input) {
        this.inputData = (T) input;
        dao = new FormatDao<>();
    }

    public FormatApi(Dao<Format> dao, Format input) {
        this.inputData = (T) input;
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Format saveFormat() {
        return null;
    }

    public Format updateFormat() {
        return null;
    }

    public Format findFormat() {
        return null;
    }

    public boolean deleteFormat() {
        return true;
    }
}
