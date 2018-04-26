package de.qucosa.oai.provider.persistence.pojos;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("pid")
    private String pid;
    
    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("sets")
    private List<String> sets = new ArrayList<String>();
    
    @JsonProperty("data")
    private Document data;
    
    @JsonProperty("modified")
    private Timestamp modified;
    
    @JsonProperty("oaiId")
    private String oaiId;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSets(List<String> sets) { this.sets = sets; }

    public void setSet(String set) { this.sets.add(set); }

    public List<String> getSets() { return sets; }

    public Document getData() {
        return data;
    }

    public void setData(Document data) {
        this.data = data;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public String getOaiId() {
        return oaiId;
    }

    public void setOaiId(String oaiId) {
        this.oaiId = oaiId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
