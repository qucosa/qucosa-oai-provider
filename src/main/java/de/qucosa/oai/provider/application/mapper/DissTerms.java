package de.qucosa.oai.provider.application.mapper;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissTerms {
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
}
