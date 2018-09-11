package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SetsToRecord implements HasIdentifier {
    private Long idSet;

    private Long idRecord;

    public Long getIdSet() {
        return idSet;
    }

    public void setIdSet(Long idSet) {
        this.idSet = idSet;
    }

    public Long getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(Long idRecord) {
        this.idRecord = idRecord;
    }

    @Override
    public void setIdentifier(Object identifier) {

    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return null;
    }
}
