package de.qucosa.oai.provider.persistence.pojos;

import java.sql.Timestamp;

public class Identifier {
    private Long id;
    
    private String identifier;
    
    private Timestamp datestamp;
    
    private String pid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Timestamp getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(Timestamp datestamp) {
        this.datestamp = datestamp;
    }
    
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
