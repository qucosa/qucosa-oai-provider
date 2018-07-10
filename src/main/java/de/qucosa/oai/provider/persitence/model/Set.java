package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "set")
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Set extends ResourceSupport implements Serializable {
    @JsonProperty("setid")
    private Long setId;

    @JsonProperty("setspec")
    private String setSpec;

    @JsonProperty("setname")
    private String setName;

    @JsonProperty("setdescription")
    private String setDescription;

    @JsonProperty("deleted")
    private boolean deleted;

    public Long getSetId() { return setId; }

    public void setSetId(Long setId) { this.setId = setId; }

    public String getSetSpec() { return setSpec; }

    public void setSetSpec(String setSpec) { this.setSpec = setSpec; }

    public String getSetName() { return setName; }

    public void setSetName(String setName) { this.setName = setName; }

    public String getSetDescription() { return setDescription; }

    public void setSetDescription(String setDescription) { this.setDescription = setDescription; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
