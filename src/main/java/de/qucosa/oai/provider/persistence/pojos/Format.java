package de.qucosa.oai.provider.persistence.pojos;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("mdprefix")
    private String mdprefix;
    
    @JsonProperty("lastpolldate")
    private Timestamp lastpolldate;
    
    @JsonProperty("dissType")
    private String dissType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMdprefix() {
        return mdprefix;
    }

    public void setMdprefix(String mdprefix) {
        this.mdprefix = mdprefix;
    }

    public Timestamp getLastpolldate() {
        return lastpolldate;
    }

    public void setLastpolldate(Timestamp lastpolldate) {
        this.lastpolldate = lastpolldate;
    }
    
    public String getDissType() {
        return dissType;
    }

    public void setDissType(String dissType) {
        this.dissType = dissType;
    }
}
