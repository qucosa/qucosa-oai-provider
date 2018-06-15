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
import de.qucosa.oai.provider.application.mapper.SetsConfig;
import org.w3c.dom.Document;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordTransport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("pid")
    private String pid;
    
    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("sets")
    private Set<SetsConfig.Set> sets = new HashSet<>();
    
    @JsonProperty("data")
    private Document data;
    
    @JsonProperty("modified")
    private Timestamp modified;
    
    @JsonProperty("oaiid")
    private String oaiId;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Set<SetsConfig.Set> getSets() { return sets; }

    public void setSets(Set<SetsConfig.Set> sets) { this.sets = sets; }

    public void setSet(SetsConfig.Set set) { sets.add(set); }

    public Document getData() {
        return data;
    }

    public void setData(Document data) {
        this.data = data;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public String getOaiId() {
        return oaiId;
    }

    public void setOaiId(String oaiId) {
        this.oaiId = oaiId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
