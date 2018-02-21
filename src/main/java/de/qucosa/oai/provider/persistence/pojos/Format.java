package de.qucosa.oai.provider.persistence.pojos;

public class Format {
    private Long id;
    
    private String mdprefix;
    
    private String method;
    
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
