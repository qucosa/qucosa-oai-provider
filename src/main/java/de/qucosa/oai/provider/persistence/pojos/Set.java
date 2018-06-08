/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.persistence.pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

@XmlRootElement(name = "set")
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Set implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("setSpec")
    private String setSpec;
    
    @JsonProperty("setName")
    private String setName;

    @JsonProperty("setDescription")
    private String setDescription;

    @JsonProperty("deleted")
    private boolean deleted;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetSpec() {
        return setSpec;
    }

    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }
    
    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetDescription() { return setDescription; }

    public void setSetDescription(String setDescription) { this.setDescription = setDescription; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
