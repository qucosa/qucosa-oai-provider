package de.qucosa.oai.provider.config.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

@Configuration
public class SetJson {
    private Logger logger = LoggerFactory.getLogger(SetJson.class);

    private List<de.qucosa.oai.provider.config.mapper.SetJson.Set> mapping;

    private InputStream config;

    public SetJson(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public SetJson(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public SetJson(InputStream stream) {
        config = stream;
        ObjectMapper om = new ObjectMapper();

        try {
            mapping = om.readValue(stream, om.getTypeFactory().constructCollectionType(List.class, de.qucosa.oai.provider.config.mapper.SetJson.Set.class));
        } catch (IOException e) {
            logger.error("Cannot parse list-set-conf JSON file.", e);
        }
    }

    public List<de.qucosa.oai.provider.config.mapper.SetJson.Set> getSetObjects() { return mapping; }

    public de.qucosa.oai.provider.config.mapper.SetJson.Set getSetObject(String setSpec) {
        de.qucosa.oai.provider.config.mapper.SetJson.Set setObj = null;

        for (de.qucosa.oai.provider.config.mapper.SetJson.Set obj : getSetObjects()) {

            if (obj.getSetSpec().equals(setSpec)) {
                setObj = obj;
                break;
            }
        }

        return setObj;
    }

    public java.util.Set<String> getSetSpecs() {
        java.util.Set<String> setSpecs = new HashSet<String>();

        for (int i = 0; i < getSetObjects().size(); i++) {
            de.qucosa.oai.provider.config.mapper.SetJson.Set set = getSetObjects().get(i);
            setSpecs.add(set.getSetSpec());
        }

        return setSpecs;
    }
}
