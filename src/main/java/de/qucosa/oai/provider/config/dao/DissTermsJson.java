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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class DissTermsJson {
    private Logger logger = LoggerFactory.getLogger(DissTermsJson.class);

    private de.qucosa.oai.provider.config.mapper.DissTermsJson mapping;

    public DissTermsJson() throws FileNotFoundException {
        this("classpath:/config/dissemination-config.json");
    }

    public DissTermsJson(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public DissTermsJson(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public DissTermsJson(InputStream stream) {
        ObjectMapper om = new ObjectMapper();

        try {
            mapping = om.readValue(stream, de.qucosa.oai.provider.config.mapper.DissTermsJson.class);
        } catch (IOException e) {
            logger.error("Cannot parse dissemination-conf JSON file.", e);
        }
    }

    public Map<String, String> getMapXmlNamespaces() {
        HashSet<de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace> xmlNamespaces = (HashSet<de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace>) mapping.getXmlnamespaces();
        Map<String, String> map = new HashMap<>();

        for (de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace namspace : xmlNamespaces) {
            map.put(namspace.getPrefix(), namspace.getUrl());
        }

        return map;
    }

    public Set<de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace> getSetXmlNamespaces() { return mapping.getXmlnamespaces(); }

    public de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace getXmlNamespace(String prefix) {
        de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace xmlNamspace = null;

        for (de.qucosa.oai.provider.config.mapper.DissTermsJson.XmlNamespace namespace : mapping.getXmlnamespaces()) {

            if (namespace.getPrefix().equals(prefix)) {
                xmlNamspace = namespace;
            }
        }

        return xmlNamspace;
    }

    public de.qucosa.oai.provider.config.mapper.DissTermsJson.Term getTerm(String diss, String name) {
        HashSet<de.qucosa.oai.provider.config.mapper.DissTermsJson.DissTerm> dissTerms = (HashSet<de.qucosa.oai.provider.config.mapper.DissTermsJson.DissTerm>) mapping.getDissTerms();
        de.qucosa.oai.provider.config.mapper.DissTermsJson.Term term = null;

        for (de.qucosa.oai.provider.config.mapper.DissTermsJson.DissTerm dt : dissTerms) {

            if (!dt.getDiss().equals(diss)) {
                logger.error(diss + " is does not exists in dissemination-config.");
                continue;
            }

            if (dt.getTerms().isEmpty()) {
                logger.error(diss + " has no terms config.");
                continue;
            }

            for (de.qucosa.oai.provider.config.mapper.DissTermsJson.Term t : dt.getTerms()) {

                if (!t.getName().equals(name)) {
                    logger.error("The term name " + name + " is not available in dissemination " + diss);
                    continue;
                }

                term = t;
            }

            if (term != null) {
                break;
            }
        }

        return term;
    }

    public Set<de.qucosa.oai.provider.config.mapper.DissTermsJson.DissFormat> getFormats() { return mapping.getFormats(); }

    public de.qucosa.oai.provider.config.mapper.DissTermsJson.DissFormat getFormat(String format) {
        de.qucosa.oai.provider.config.mapper.DissTermsJson.DissFormat dissFormat = null;

        for (de.qucosa.oai.provider.config.mapper.DissTermsJson.DissFormat df : mapping.getFormats()) {

            if (df.getMdprefix().equals(format)) {
                dissFormat = df;
                break;
            }
        }

        return dissFormat;
    }
}
