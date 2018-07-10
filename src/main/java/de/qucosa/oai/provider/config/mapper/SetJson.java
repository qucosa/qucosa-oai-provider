package de.qucosa.oai.provider.config.mapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetJson implements Serializable {
    @JsonProperty("sets")
    private List<Set> sets = new ArrayList<>();

    public List<Set> getSets() { return sets; }

    public void setSets(List<Set> sets) { this.sets = sets; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Set {
        @JsonProperty("setspec")
        private String setSpec;

        @JsonProperty("setname")
        private String setName;

        @JsonProperty("predicate")
        private String predicate;

        @JsonProperty("setdescription")
        private String setDescription;

        public String getSetSpec() { return setSpec; }

        public void setSetSpec(String setSpec) { this.setSpec = setSpec; }

        public String getSetName() { return setName; }

        public void setSetName(String setName) { this.setName = setName; }

        public String getPredicate() { return predicate; }

        public void setPredicate(String predicate) { this.predicate = predicate; }

        public String getSetDescription() { return setDescription; }

        public void setSetDescription(String setDescription) { this.setDescription = setDescription; }
    }
}
