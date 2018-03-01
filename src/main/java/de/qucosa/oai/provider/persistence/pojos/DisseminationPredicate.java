package de.qucosa.oai.provider.persistence.pojos;

public class DisseminationPredicate {
    private Long id;
    
    private String predicate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
}
