package de.qucosa.oai.provider.persistence.pojos;

import java.sql.Date;

public class Record {
    private Long id;
    
    private Long identifierId;
    
    private Long format;
    
    private Date moddate;
    
    private String xmldata;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdentifierId() {
        return identifierId;
    }

    public void setIdentifierId(Long identifierId) {
        this.identifierId = identifierId;
    }

    public Long getFormat() {
        return format;
    }

    public void setFormat(Long format) {
        this.format = format;
    }

    public Date getModdate() {
        return moddate;
    }

    public void setModdate(Date moddate) {
        this.moddate = moddate;
    }

    public String getXmldata() {
        return xmldata;
    }

    public void setXmldata(String xmldata) {
        this.xmldata = xmldata;
    }
}
