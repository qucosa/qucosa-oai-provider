package de.qucosa.oai.provider.persistence.pojos;

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
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("lastpolldate")
    private Long lastpolldate;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getLastpolldate() {
        return lastpolldate;
    }

    public void setLastpolldate(Long lastpolldate) {
        this.lastpolldate = lastpolldate;
    }
}
