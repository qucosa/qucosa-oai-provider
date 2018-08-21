package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format extends ResourceSupport implements Serializable {
    @JsonProperty("formatid")
    private Long formatId;

    @JsonProperty("mdprefix")
    private String mdprefix;

    @JsonProperty("schemaurl")
    private String schemaUrl;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("deleted")
    private boolean deleted;

    public Long getFormatId() { return formatId; }

    public void setFormatId(Long formatId) { this.formatId = formatId; }

    public String getMdprefix() { return mdprefix; }

    public void setMdprefix(String mdprefix) { this.mdprefix = mdprefix; }

    public String getSchemaUrl() { return schemaUrl; }

    public void setSchemaUrl(String schemaUrl) { this.schemaUrl = schemaUrl; }

    public String getNamespace() { return namespace; }

    public void setNamespace(String namespace) { this.namespace = namespace; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
