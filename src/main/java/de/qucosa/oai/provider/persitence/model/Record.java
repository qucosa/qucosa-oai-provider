package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record extends ResourceSupport implements Serializable, HasIdentifier {
    @JsonProperty("recordid")
    private Long recordId;

    @JsonProperty("pid")
    private String pid;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("deleted")
    private boolean deleted;

    @Override
    public void setIdentifier(Object identifier) {
        setRecordId(recordId);
    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return getRecordId();
    }

    public Long getRecordId() { return recordId; }

    public void setRecordId(Long recordId) {  }

    public String getPid() { return pid; }

    public void setPid(String pid) { this.pid = pid; }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
