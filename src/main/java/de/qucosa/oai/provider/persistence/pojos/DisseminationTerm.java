package de.qucosa.oai.provider.persistence.pojos;

public class DisseminationTerm {
    private Long id;
    
    private Long formatId;
    
    private Long dissPredicateId;
    
    private String term;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFormatId() {
        return formatId;
    }

    public void setFormatId(Long formatId) {
        this.formatId = formatId;
    }

    public Long getDissPredicateId() {
        return dissPredicateId;
    }

    public void setDissPredicateId(Long dissPredicateId) {
        this.dissPredicateId = dissPredicateId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
