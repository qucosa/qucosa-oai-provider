package de.qucosa.oai.provider.persitence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTransport implements Serializable {
    @JsonProperty("record")
    private Record record;

    @JsonProperty("format")
    private Format format;

    @JsonProperty("dissemination")
    private Dissemination dissemination;
    @JsonProperty("sets")
    private List<Set> sets;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Dissemination getDissemination() {
        return dissemination;
    }

    public void setDissemination(Dissemination dissemination) {
        this.dissemination = dissemination;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
}
