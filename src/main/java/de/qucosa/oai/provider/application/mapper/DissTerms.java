package de.qucosa.oai.provider.application.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissTerms {
    @JsonIgnore
    private DissTermsDao dao = null;
    
    @JsonProperty("xmlnamespacees")
    private Set<XmlNamspace> xmlnamespacees;

    @JsonProperty("dissTerms")
    private Set<DissTerm> dissTerms;
    
    @JsonProperty("formats")
    private Set<DissFormat> formats;

    public Set<XmlNamspace> getXmlnamespacees() {
        return xmlnamespacees;
    }

    public void setXmlnamespacees(Set<XmlNamspace> xmlnamespacees) {
        this.xmlnamespacees = xmlnamespacees;
    }

    public Set<DissTerm> getDissTerms() {
        return dissTerms;
    }

    public void setDissTerms(Set<DissTerm> dissTerms) {
        this.dissTerms = dissTerms;
    }
    
    public Set<DissFormat> getFormats() {
        return formats;
    }

    public void setFormats(Set<DissFormat> formats) {
        this.formats = formats;
    }

    public static class XmlNamspace {
        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("url")
        private String url;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class DissTerm {
        @JsonProperty("diss")
        private String diss;

        @JsonProperty("terms")
        private Set<Term> terms;

        public String getDiss() {
            return diss;
        }

        public void setDiss(String diss) {
            this.diss = diss;
        }

        public Set<Term> getTerms() {
            return terms;
        }

        public void setTerms(Set<Term> terms) {
            this.terms = terms;
        }
    }

    public static class Term {
        @JsonProperty("name")
        private String name;

        @JsonProperty("term")
        private String term;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }
    }
    
    public static class DissFormat {
        @JsonProperty("format")
        private String format;
        
        @JsonProperty("dissType")
        private String dissType;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getDissType() {
            return dissType;
        }

        public void setDissType(String dissType) {
            this.dissType = dissType;
        }
    }
    
    @JsonIgnore
    public Map<String, String> getMapXmlNamespaces() {
        return dao().getMapXmlNamespaces();
    }
    
    public Set<XmlNamspace> getSetXmlNamespaces() {
        return dao().getSetXmlNamespaces();
    }
    
    @JsonIgnore
    public XmlNamspace getXmlNamespace(String prefix) {
        return dao().getXmlNamespace(prefix);
    }
    
    @JsonIgnore
    public Term getTerm(String diss, String name) {
        return dao().getTerm(diss, name);
    }
    
    public Set<DissTerm> getTerms() {
        return dao().getDissTerms();
    }
    
    public Set<DissFormat> formats() {
        return dao().formats();
    }
    
    private DissTermsDao dao() {
        
        if (dao == null) {
            dao = new DissTermsDao();
        }
        
        return dao;
    }
    
    public static class DissTermsDao {
        private final Logger logger = LoggerFactory.getLogger(DissTermsDao.class);

        DissTerms dissTerms = null;

        public DissTermsDao() {
            ObjectMapper om = new ObjectMapper();
            File file = new File("/home/dseelig/opt/oaiprovider/config/dissemination-config.json");

            try {
                dissTerms = om.readValue(Files.readAllBytes(Paths.get(file.getAbsolutePath())), DissTerms.class);
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug("dissemination-conf parse failed.");
            }
        }

        public Map<String, String> getMapXmlNamespaces() {
            HashSet<XmlNamspace> xmlNamespaces = (HashSet<XmlNamspace>) dissTerms.getXmlnamespacees();
            Map<String, String> map = new HashMap<>();

            for (XmlNamspace namspace : xmlNamespaces) {
                map.put(namspace.getPrefix(), namspace.getUrl());
            }

            return map;
        }

        public Set<XmlNamspace> getSetXmlNamespaces() {
            return xmlNamespaces();
        }

        public XmlNamspace getXmlNamespace(String prefix) {
            XmlNamspace xmlNamspace = null;

            for (XmlNamspace namespace : xmlNamespaces()) {

                if (namespace.getPrefix().equals(prefix)) {
                    xmlNamspace = namespace;
                }
            }

            return xmlNamspace;
        }

        public Term getTerm(String diss, String name) {
            HashSet<DissTerm> dissTerms = (HashSet<DissTerm>) this.dissTerms.getDissTerms();
            Term term = null;

            for (DissTerm dt : dissTerms) {

                if (!dt.getDiss().equals(diss)) {
                    logger.debug(diss + " is does not exists in dissemination-config.");
                    continue;
                }

                if (dt.getTerms().isEmpty()) {
                    logger.debug(diss + " has no terms config.");
                    continue;
                }

                for (Term t : dt.getTerms()) {

                    if (!t.getName().equals(name)) {
                        logger.debug("The term name " + name + " is not available in dissemination " + diss);
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
        
        public Set<DissTerm> getDissTerms() {
            return this.dissTerms.getDissTerms();
        }
        
        public Set<DissFormat> formats() {
            return dissTerms.getFormats();
        }
        
        public DissFormat format(String format) {
            DissFormat dissFormat = null;
            
            for (DissFormat df : dissTerms.getFormats()) {
                
                if (df.getFormat().equals(format)) {
                    dissFormat = df;
                    break;
                }
            }
            
            return dissFormat;
        }

        private Set<XmlNamspace> xmlNamespaces() {
            HashSet<XmlNamspace> xmlNamespaces = (HashSet<XmlNamspace>) dissTerms.getXmlnamespacees();
            return xmlNamespaces;
        }
    }
}
