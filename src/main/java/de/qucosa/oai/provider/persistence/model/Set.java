/*
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.persistence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "set")
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Set implements Serializable, HasIdentifier {
    @JsonProperty("setid")
    private Long setId;

    @JsonProperty("setspec")
    private String setSpec;

    @JsonProperty("setname")
    private String setName;

    @JsonProperty("setdescription")
    private String setDescription;

    @JsonProperty("deleted")
    private boolean deleted;

    @Override
    public void setIdentifier(Object identifier) {
        setSetId(Long.parseLong(String.valueOf(identifier)));
    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return getSetId();
    }

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public String getSetSpec() { return setSpec; }

    public void setSetSpec(String setSpec) { this.setSpec = setSpec; }

    public String getSetName() { return setName; }

    public void setSetName(String setName) { this.setName = setName; }

    public String getSetDescription() { return setDescription; }

    public void setSetDescription(String setDescription) { this.setDescription = setDescription; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
