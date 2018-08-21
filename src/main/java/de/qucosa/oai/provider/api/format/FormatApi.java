package de.qucosa.oai.provider.api.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.model.Format;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Component
public class FormatApi<T> {

    private Dao dao;

    private T inputData;

    public FormatApi() {}

    public FormatApi(T input) throws IOException {
        if (input instanceof String) {
            ObjectMapper om = new ObjectMapper();
            String ip = (String) input;

            try {
                inputData = om.readValue(ip.getBytes("UTF-8"), om.getTypeFactory().constructCollectionType(List.class, Format.class));
            } catch (IOException e) {

                try {
                    inputData = (T) om.readValue(ip.getBytes("UTF-8"), Format.class);
                } catch (IOException e1) {
                    throw e;
                }
            }
        }

        if (input instanceof Format) {
            inputData = input;
        }

        if (input instanceof List) {
            inputData = input;
        }

        setDao(new FormatDao<Format>());
    }

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public T getInputData() {
        return inputData;
    }

    public Format saveFormat(Format format) throws SQLException {
        return (Format) dao.save(format);
    }

    public List<Format> saveFormats(List<Format> formats) throws SQLException {
        return dao.save(formats);
    }

    public Format updateFormat(Format input, String mdprefix) throws Exception {

        if (!input.getMdprefix().equals(mdprefix)) {
            throw new Exception("Prameter mdprefix is unequal with mdprefix from format object.");
        }

        return (Format) dao.update(input);
    }

    public Format find(String column, String value) throws SQLException {
        return (Format) dao.findByColumnAndValue(column, value);
    }

    public List<Format> findAll() throws SQLException {
        return (List<Format>) dao.findAll();
    }

    public Format deleteFormat(String column, T ident, boolean value) throws SQLException {
        return (Format) dao.delete(column, ident, value);
    }
}
