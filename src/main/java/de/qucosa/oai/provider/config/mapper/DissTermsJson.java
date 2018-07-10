package de.qucosa.oai.provider.config.mapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DissTermsJson implements Serializable {
    @JsonProperty("xmlnamespaces")
    private Set<XmlNamespace> xmlnamespaces;

    @JsonProperty("dissTerms")
    private Set<DissTerm> dissTerms;

    @JsonProperty("formats")
    private Set<DissFormat> formats;

    public Set<XmlNamespace> getXmlnamespaces() { return xmlnamespaces; }

    public void setXmlnamespaces(Set<XmlNamespace> xmlnamespaces) { this.xmlnamespaces = xmlnamespaces; }

    public Set<DissTerm> getDissTerms() { return dissTerms; }

    public void setDissTerms(Set<DissTerm> dissTerms) { this.dissTerms = dissTerms; }

    public Set<DissFormat> getFormats() { return formats; }

    public void setFormats(Set<DissFormat> formats) { this.formats = formats; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class XmlNamespace {
        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("url")
        private String url;

        public String getPrefix() { return prefix; }

        public void setPrefix(String prefix) { this.prefix = prefix; }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DissTerm {
        @JsonProperty("diss")
        private String diss;

        @JsonProperty("terms")
        private Set<Term> terms;

        public String getDiss() { return diss; }

        public void setDiss(String diss) { this.diss = diss; }

        public Set<Term> getTerms() { return terms; }

        public void setTerms(Set<Term> terms) { this.terms = terms; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Term {
        @JsonProperty("name")
        private String name;

        @JsonProperty("term")
        private String term;

        public String getName() { return name; }

        public void setName(String name) { this.name = name; }

        public String getTerm() { return term; }

        public void setTerm(String term) { this.term = term; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DissFormat {
        @JsonProperty("mdprefix")
        private String mdprefix;

        @JsonProperty("schemaurl")
        private String schemaUrl;

        @JsonProperty("namespace")
        private String namespace;

        public String getMdprefix() { return mdprefix; }

        public void setMdprefix(String mdprefix) { this.mdprefix = mdprefix; }

        public String getSchemaUrl() { return schemaUrl; }

        public void setSchemaUrl(String schemaUrl) { this.schemaUrl = schemaUrl; }

        public String getNamespace() { return namespace; }

        public void setNamespace(String namespace) { this.namespace = namespace; }
    }
}
