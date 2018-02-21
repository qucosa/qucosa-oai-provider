package de.qucosa.oai.provider.persistence.pojos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name = "set")
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Set implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("stespec")
    private String stespec;
    
    @JsonProperty("setName")
    private String setName;
    
    @JsonProperty("predicate")
    private String predicate;
    
    @JsonProperty("doc")
    private String doc;

    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement
    public String getStespec() {
        return stespec;
    }

    public void setStespec(String stespec) {
        this.stespec = stespec;
    }
    
    @XmlElement
    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    @XmlTransient
    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    @XmlTransient
    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
}
