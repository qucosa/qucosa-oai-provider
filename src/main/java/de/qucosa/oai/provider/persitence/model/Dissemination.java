package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;
import java.sql.Timestamp;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dissemination extends ResourceSupport implements Serializable {
    @JsonProperty("dissid")
    private Long dissId;

    @JsonProperty("recordid")
    private Long recordId;

    @JsonProperty("formatid")
    private Long formatId;

    @JsonProperty("lastmoddate")
    private Timestamp lastmoddate;

    @JsonProperty("xmldata")
    private String xmldata;

    @JsonProperty("deleted")
    private boolean deleted;

    public Long getDissId() { return dissId; }

    public void setDissId(Long dissId) { this.dissId = dissId; }

    public Long getRecordId() { return recordId; }

    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public Long getFormatId() { return formatId; }

    public void setFormatId(Long formatId) { this.formatId = formatId; }

    public Timestamp getLastmoddate() { return lastmoddate; }

    public void setLastmoddate(Timestamp lastmoddate) { this.lastmoddate = lastmoddate; }

    public String getXmldata() { return xmldata; }

    public void setXmldata(String xmldata) { this.xmldata = xmldata; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
